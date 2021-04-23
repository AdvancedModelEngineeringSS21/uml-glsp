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

import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.operations.CreateEdgeOperation;
import org.eclipse.glsp.server.operations.Operation;
import org.eclipse.glsp.server.protocol.GLSPServerException;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
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
      Types.INCLUDE);

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
   private boolean isLinkableUCD(final String edgeType, final Classifier source, final Classifier target) {
      switch (edgeType) {
         case Types.ASSOCIATION:
            return (source instanceof Actor || source instanceof UseCase)
               && (target instanceof Actor || target instanceof UseCase)
               && !(source instanceof Actor && target instanceof Actor);
         case Types.EXTEND:
            return (source instanceof UseCase && target instanceof UseCase);
         case Types.INCLUDE:
            return (source instanceof UseCase && target instanceof UseCase);
         case Types.GENERALIZATION:
            return (source instanceof UseCase && target instanceof UseCase)
               || (source instanceof Actor && target instanceof Actor);
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

      Classifier sourceClassifier = getOrThrow(modelIndex.getSemantic(operation.getSourceElementId(), Classifier.class),
         "No semantic Element found for source element with id " + operation.getSourceElementId());
      Classifier targetClassifier = getOrThrow(modelIndex.getSemantic(operation.getTargetElementId(), Classifier.class),
         "No semantic Element found for target element with id" + operation.getTargetElementId());

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
            modelAccess.addAssociation(modelState, sourceClassifier, targetClassifier)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new UCD Association edge");
                  }
               });
         }

      } else if (elementTypeId.equals(Types.EXTEND)) {
         if (!(isLinkableUCD(Types.EXTEND, sourceClassifier, targetClassifier))) {
            throw new GLSPServerException(
               "Could not execute create operation on new UCD Extend edge - source and target need to be Usecases!");
         }
         modelAccess.addExtend(modelState, (UseCase) sourceClassifier, (UseCase) targetClassifier)
            .thenAccept(response -> {
               if (!response.body()) {
                  throw new GLSPServerException("Could not execute create operation on new UCD Extend edge");
               }
            });

      } else if (elementTypeId.equals(Types.INCLUDE)) {
         if (!(isLinkableUCD(Types.INCLUDE, sourceClassifier, targetClassifier))) {
            throw new GLSPServerException(
               "Could not execute create operation on new UCD Include edge - source and target need to be Usecases!");
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
               "Could not execute create operation on new UCD Include edge - source and target need to be Usecases!");
         }
         modelAccess.addAssociation(modelState, sourceClassifier, targetClassifier)
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
