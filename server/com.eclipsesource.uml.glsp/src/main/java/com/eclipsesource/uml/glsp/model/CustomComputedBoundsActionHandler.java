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

public class CustomComputedBoundsActionHandler extends ComputedBoundsActionHandler {

   @Override
   public List<Action> executeAction(final ComputedBoundsAction action, final GModelState modelState) {

      System.out.println("CustomComputedBoundsActionHandler");

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

   public void modifyModelRoot(final GModelState modelState) {
      UmlModelState state = UmlModelState.getModelState(modelState);
      GModelRoot root = state.getRoot();
      GModelIndex index = modelState.getIndex();
      for (GModelElement el : root.getChildren()) {
         System.out.println(el.getId());
         GModelElement element = getOrThrow(index.get(el.getId()),
            "Model element not found! ID: " + el.getId());
         if (element instanceof GBoundsAware) {
            GBoundsAware bae = (GBoundsAware) element;
            if (((GNode) bae).getType() == Types.USECASE) {
               System.out.println("Found use case " + el.getId());
               final double UC_SCALE_FACTOR = 1.4;
               GDimension size = bae.getSize();
               size.setHeight(size.getHeight() * UC_SCALE_FACTOR);
               size.setWidth(size.getWidth() * UC_SCALE_FACTOR);
               bae.setSize(size);
               // GPoint newPos = bae.getPosition();
               // newPos.setX(newPos.getX() - size.getWidth() / 2);
               // newPos.setY(newPos.getY() + size.getWidth() / 2);
               for (GModelElement child : element.getChildren()) {
                  if (child instanceof GBoundsAware) {
                     GBoundsAware baeChild = (GBoundsAware) child;
                     GPoint pos = baeChild.getPosition();
                     pos.setX(size.getWidth() / 2 - baeChild.getSize().getWidth() / 2);
                     pos.setY(pos.getY() + 10);
                     baeChild.setPosition(pos);
                  }
               }
               System.out.println(bae);

            }
         }
      }
   }

}
