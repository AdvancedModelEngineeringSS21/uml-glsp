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
package com.eclipsesource.uml.glsp.operations;

import static org.eclipse.glsp.server.protocol.GLSPServerException.getOrThrow;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.operations.CreateEdgeOperation;
import org.eclipse.glsp.server.operations.Operation;
import org.eclipse.glsp.server.protocol.GLSPServerException;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.ExtensionPoint;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.glsp.model.UmlModelIndex;
import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.modelserver.UmlModelServerAccess;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.google.common.collect.Lists;

public class CreateEdgeOperationHandler extends ModelServerAwareBasicCreateOperationHandler<CreateEdgeOperation> {

   public CreateEdgeOperationHandler() {
      super(handledElementTypeIds);
   }

   private static List<String> handledElementTypeIds = Lists.newArrayList(Types.ASSOCIATION, Types.EXTEND,
      Types.INCLUDE, Types.GENERALIZATION, Types.COMMENT_EDGE);

   @Override
   public boolean handles(final Operation execAction) {
      if (execAction instanceof CreateEdgeOperation) {
         CreateEdgeOperation action = (CreateEdgeOperation) execAction;
         return handledElementTypeIds.contains(action.getElementTypeId());
      }
      return false;
   }

   /**
    * checks if two Use case diagram elements may be linked using the specified edgeType in the context of the use case
    * diagram.
    *
    * @param edgeType
    * @param source
    * @param target
    * @return
    */
   private boolean isLinkableUCD(final String edgeType, final EObject source, final EObject target) {

      if (source.equals(target)) {
         return false;
      }

      switch (edgeType) {
         case Types.ASSOCIATION:
            return (source instanceof Actor || source instanceof UseCase)
               && (target instanceof Actor || target instanceof UseCase)
               && !(source instanceof Actor && target instanceof Actor);
         case Types.EXTEND:
            return ((source instanceof UseCase && target instanceof UseCase)
               || (target instanceof ExtensionPoint && source instanceof UseCase));
         case Types.INCLUDE:
            return (source instanceof UseCase && target instanceof UseCase);
         case Types.GENERALIZATION:
            return (source instanceof UseCase && target instanceof UseCase)
               || (source instanceof Actor && target instanceof Actor);
         case Types.COMMENT_EDGE:
            return (source instanceof Comment && target instanceof Extend) ||
               (source instanceof Comment && target instanceof Classifier);
         // TODO: Check Classifier or other Type
         default:
            return false;
      }
   }

   @Override
   public void executeOperation(final CreateEdgeOperation operation, final GModelState graphicalModelState,
      final UmlModelServerAccess modelAccess) throws Exception {
      String elementTypeId = operation.getElementTypeId();

      UmlModelState modelState = UmlModelState.getModelState(graphicalModelState);
      UmlModelIndex modelIndex = modelState.getIndex();

      String target = operation.getTargetElementId();
      if (target.endsWith("_anchor")) {
         String pattern = "(.*)_anchor";

         // Create a Pattern object
         Pattern r = Pattern.compile(pattern);

         // Now create matcher object.
         Matcher m = r.matcher(target);

         if (m.find()) {
            target = m.group(1);
         }
      }

      EObject sourceClassifier = getOrThrow(modelIndex.getSemantic(operation.getSourceElementId()),
         "No semantic Element found for source element with id " + operation.getSourceElementId());
      EObject targetClassifier = getOrThrow(modelIndex.getSemantic(target),
         "No semantic Element found for target element with id " + operation.getTargetElementId());
      // Classifier targetClassifier = getOrThrow(modelIndex.getSemantic(operation.getTargetElementId(),
      // Classifier.class),
      // "No semantic Element found for target element with id" + operation.getTargetElementId());

      if (elementTypeId.equals(Types.ASSOCIATION)) {
         // Case Base Class diagram implementation
         if (sourceClassifier.getClass().equals(targetClassifier.getClass()) && targetClassifier instanceof Class) {
            modelAccess.addAssociation(modelState, (Class) sourceClassifier, (Class) targetClassifier)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new Association edge");
                  }
               });
         } else if (isLinkableUCD(Types.ASSOCIATION, sourceClassifier, targetClassifier)) {
            modelAccess.addAssociation(modelState, (Class) sourceClassifier, (Class) targetClassifier)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new UCD Association edge");
                  }
               });
         }

      } else if (elementTypeId.equals(Types.EXTEND)) {
         if (!(isLinkableUCD(Types.EXTEND, sourceClassifier, targetClassifier))) {
            throw new GLSPServerException(
               "Could not execute create operation on new UCD Extend edge - source and target need to be different Usecases or a usecase and an existing extension point!");
         }
         if (targetClassifier instanceof ExtensionPoint) {
            modelAccess.addExtend(modelState, (UseCase) sourceClassifier, (ExtensionPoint) targetClassifier)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new UCD Extend edge");
                  }
               });
         } else {
            modelAccess.addExtend(modelState, (UseCase) sourceClassifier, (UseCase) targetClassifier)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new UCD Extend edge");
                  }
               });
         }

      } else if (elementTypeId.equals(Types.INCLUDE)) {
         if (!(isLinkableUCD(Types.INCLUDE, sourceClassifier, targetClassifier))) {
            throw new GLSPServerException(
               "Could not execute create operation on new UCD Include edge - source and target need to be different Usecases!");
         }
         modelAccess.addInclude(modelState, (UseCase) sourceClassifier, (UseCase) targetClassifier)
            .thenAccept(response -> {
               if (!response.body()) {
                  throw new GLSPServerException("Could not execute create operation on new UCD Include edge");
               }
            });
      } else if (elementTypeId.equals(Types.GENERALIZATION)) {
         if (!(isLinkableUCD(Types.GENERALIZATION, sourceClassifier, targetClassifier))) {
            throw new GLSPServerException(
               "Could not execute create operation on new UCD Generalization edge - source and target need to be different elements of the same type!");
         }
         modelAccess.addGeneralization(modelState, (Classifier) sourceClassifier, (Classifier) targetClassifier)
            .thenAccept(response -> {
               if (!response.body()) {
                  throw new GLSPServerException("Could not execute create operation on new UCD Generalization edge");
               }
            });
      } else if (elementTypeId.equals(Types.COMMENT_EDGE)) {
         if (!(isLinkableUCD(Types.COMMENT_EDGE, sourceClassifier, targetClassifier))) {
            throw new GLSPServerException(
               "Could not execute create operation on new UCD Generalization edge - source and target need to be different elements of the same type!");
         }
         modelAccess.addCommentEdge(modelState, (Comment) sourceClassifier, targetClassifier)
            .thenAccept(response -> {
               if (!response.body()) {
                  throw new GLSPServerException("Could not execute create operation on new UCD Generalization edge");
               }
            });
      }
   }

   @Override
   public String getLabel() { return "Create uml edge"; }

}
