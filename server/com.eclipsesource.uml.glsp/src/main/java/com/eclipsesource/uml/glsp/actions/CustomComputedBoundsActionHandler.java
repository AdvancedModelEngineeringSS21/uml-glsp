/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package com.eclipsesource.uml.glsp.model;

import static org.eclipse.glsp.server.protocol.GLSPServerException.getOrThrow;

import java.util.List;

import org.eclipse.glsp.graph.GBoundsAware;
import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GDimension;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GModelIndex;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.server.actions.Action;
import org.eclipse.glsp.server.features.core.model.ComputedBoundsAction;
import org.eclipse.glsp.server.features.core.model.ComputedBoundsActionHandler;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.utils.LayoutUtil;

import com.eclipsesource.uml.glsp.gmodel.GModelFactory;
import com.eclipsesource.uml.glsp.gmodel.GModelFactoryProvider;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;

/**
 * The CustomComputedBoundsActionHandler hooks into the request pipeline that is triggered at each model change.
 * We use it to make changes to the size and/or position of various model elements, for example Packages, in
 * consideration of
 * the respective sizes of their child elements.
 *
 * @author Lukas Genssler
 * @author Felix Winterleitner
 *
 */
public class CustomComputedBoundsActionHandler extends ComputedBoundsActionHandler {

   @Override
   public List<Action> executeAction(final ComputedBoundsAction action, final GModelState modelState) {
      synchronized (submissionHandler.getModelLock()) {
         GModelRoot model = modelState.getRoot();
         if (model != null && model.getRevision() == action.getRevision()) {
            LayoutUtil.applyBounds(model, action, modelState);
            // invoke a method that runs through the gmodel and adjust the values again
            modifyModelRoot(modelState);
            // and finally send the UpdateModelAction/SetModelAction.
            return submissionHandler.doSubmitModel(true, modelState);
         }
      }
      return none();
   }

   public void updateBounds(final GModelState modelState) {
      UmlModelState state = UmlModelState.getModelState(modelState);
      GModelFactory factory = GModelFactoryProvider.get(state);
      GModelRoot gmodelRoot = factory.create();
      modelState.setRoot(gmodelRoot);
   }

   /**
    * Iterates over the Model child Elements and performs resizing and repositioning if necessary.
    *
    * @param modelState
    */
   public void modifyModelRoot(final GModelState modelState) {
      UmlModelState state = UmlModelState.getModelState(modelState);
      GModelRoot root = state.getRoot();
      GModelIndex index = modelState.getIndex();
      GDimension size;
      for (GModelElement el : root.getChildren()) {
         GModelElement element = getOrThrow(index.get(el.getId()),
            "Model element not found! ID: " + el.getId());
         if (element instanceof GBoundsAware) {
            GBoundsAware bae = (GBoundsAware) element;
            switch (((GNode) bae).getType()) {
               case Types.USECASE:
                  // Make use case bigger to reduce possibility for text overflow
                  adjustUseCaseSize(element);
                  break;
               case Types.COMPONENT:
                  adjustComponentOrPackageSize(element);
                  break;
               case Types.PACKAGE:
                  adjustComponentOrPackageSize(element);
                  break;
               case Types.ACTOR:
                  // TODO: CHECK WHY THIS DOES NOTHING
                  GModelElement textElement = element.getChildren().get(0).getChildren().get(0);
                  if (textElement instanceof GBoundsAware) {
                     GBoundsAware textBae = (GBoundsAware) textElement;
                     GPoint textPos = textBae.getPosition();
                     textPos.setX(
                        bae.getPosition().getX() + bae.getSize().getWidth() / 2 - textBae.getSize().getWidth() / 2);
                     textPos.getX();
                     // textPos.setX(bae.getSize().getWidth() / 2 - textBae.getSize().getWidth() / 2);
                  }
                  break;
            }
         }
      }
   }

   /**
    * Adjusts the Size of UseCase ellipses to reduce the number of occurrences of text overflow.
    *
    * @param element
    */
   public void adjustUseCaseSize(final GModelElement element) {
      // Make use case bigger to reduce possibility for text overflow
      GBoundsAware bae = (GBoundsAware) element;

      final double UC_SCALE_FACTOR = 1.4;
      GDimension size = bae.getSize();
      size.setHeight(size.getHeight() * UC_SCALE_FACTOR);
      size.setWidth(size.getWidth() * UC_SCALE_FACTOR);
      bae.setSize(size);
      for (GModelElement child : element.getChildren()) {
         if (child instanceof GBoundsAware) {
            GBoundsAware baeChild = (GBoundsAware) child;
            GPoint pos = baeChild.getPosition();
            pos.setX(size.getWidth() / 2 - baeChild.getSize().getWidth() / 2);
            pos.setY(pos.getY() + 10);
            baeChild.setPosition(pos);
         }
      }
   }

   /**
    * Adjusts the size and/or position of a Component or Package depeding on size and position of the child elements.
    *
    * @param element
    */
   public void adjustComponentOrPackageSize(final GModelElement element) {

      GBoundsAware bae = (GBoundsAware) element;
      GPoint compPosition = bae.getPosition();
      GDimension compSize = bae.getSize();
      double X = 0;
      double Y = 0;
      double W = compSize.getWidth();
      double H = compSize.getHeight();
      double headerHeight = 38;
      double headerWidth = 30;
      final double MARGIN = 20;

      boolean changedPosition = false;
      boolean changedSize = false;

      for (GModelElement packageElement : element.getChildren()) {
         // Header Compartment
         if (packageElement instanceof GCompartment && packageElement.getId().endsWith("header")) {
            GBoundsAware baeCompartment = (GBoundsAware) packageElement;
            headerHeight = baeCompartment.getSize().getHeight();
            headerWidth = baeCompartment.getSize().getWidth();
         }
         // Child Compartment
         if (packageElement instanceof GCompartment && !packageElement.getId().contains("header")) {
            GBoundsAware baeCompartment = (GBoundsAware) packageElement;
            W = baeCompartment.getSize().getWidth();
            H = baeCompartment.getSize().getHeight();
            // iterate over children to adjust position and size
            for (GModelElement child : packageElement.getChildren()) {
               if (child.getType() == Types.USECASE) {
                  // Adjust also for nested usecases
                  adjustUseCaseSize(child);
               }
               if (child.getType() == Types.COMPONENT) {
                  adjustComponentOrPackageSize(child);
               }
               if (child instanceof GBoundsAware) {
                  GBoundsAware baeChild = (GBoundsAware) child;
                  GPoint childPos = baeChild.getPosition();
                  GDimension childSize = baeChild.getSize();
                  // if the leftmost child is left of the parent, move parent X to child X
                  if (childPos.getX() < X) {
                     X = childPos.getX();
                     changedPosition = true;
                  }
                  // if the topmost child is top of the parent, move parent Y to child Y
                  if (childPos.getY() < Y) {
                     Y = childPos.getY();
                     changedPosition = true;
                  }
                  // if the rightmost/widest child width plus the relative X position of child is larger than the parent
                  // width = if the
                  // child stands out at the right,
                  // make the width big enough to fit the children
                  if (childPos.getX() + childSize.getWidth() > W) {
                     W = childPos.getX() + childSize.getWidth();
                     changedSize = true;
                  }
                  // if the highest child height plus the relative Y position of child is larger than the parent height
                  // = if the
                  // child stands out at the bottom,
                  // make the height big enough to fit the children
                  if (childPos.getY() + childSize.getHeight() + headerHeight > H) {
                     H = childPos.getY() + childSize.getHeight() + headerHeight;
                     changedSize = true;
                  }
               }
            }
         }
      }
      if (changedSize || changedPosition) {
         /* START PFUSCH */
         double adjustment = (headerWidth - 210) * 0.66;
         if (adjustment < 0) {
            adjustment = 0;
         }
         /* END PFUSCH */
         compSize.setWidth((W + (-1) * X + MARGIN) + adjustment);
         if (compSize.getWidth() < headerWidth + MARGIN) {
            compSize.setWidth(headerWidth + MARGIN);
         }
         compSize.setHeight(H + (-1) * Y + MARGIN);
         compPosition.setX(compPosition.getX() + X);
         compPosition.setY(compPosition.getY() + Y);

         for (GModelElement packageElement : element.getChildren()) {
            if (packageElement instanceof GCompartment && !packageElement.getId().contains("header")) {
               for (GModelElement child : packageElement.getChildren()) {
                  if (child instanceof GBoundsAware) {
                     GBoundsAware baeChild = (GBoundsAware) child;
                     GPoint childPos = baeChild.getPosition();
                     if (changedPosition) {
                        childPos.setX(childPos.getX() - X);
                        childPos.setY(childPos.getY() - Y);
                     }

                  }
               }
            }
         }
      }
   }
}
