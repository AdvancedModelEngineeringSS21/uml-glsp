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

import org.apache.log4j.Logger;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.operations.CreateNodeOperation;
import org.eclipse.glsp.server.operations.Operation;
import org.eclipse.glsp.server.protocol.GLSPServerException;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.modelserver.UmlModelServerAccess;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.google.common.collect.Lists;

public class CreateClassifierNodeOperationHandler
   extends ModelServerAwareBasicCreateOperationHandler<CreateNodeOperation> {

   private static Logger LOGGER = Logger.getLogger(CreateClassifierNodeOperationHandler.class);

   public CreateClassifierNodeOperationHandler() {
      super(handledElementTypeIds);
   }

   private static List<String> handledElementTypeIds = Lists.newArrayList(Types.COMPONENT, Types.PACKAGE, Types.ACTOR,
      Types.USECASE);

   @Override
   public boolean handles(final Operation execAction) {
      if (execAction instanceof CreateNodeOperation) {
         CreateNodeOperation action = (CreateNodeOperation) execAction;
         return handledElementTypeIds.contains(action.getElementTypeId());
      }
      return false;
   }

   @Override
   public void executeOperation(final CreateNodeOperation operation, final GModelState modelState,
      final UmlModelServerAccess modelAccess) throws Exception {

      switch (operation.getElementTypeId()) {
         case Types.CLASS: {
            modelAccess.addClass(UmlModelState.getModelState(modelState), operation.getLocation())
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new Class node");
                  }
               });
            break;
         }
         case Types.PACKAGE: {
            modelAccess.addPackage(UmlModelState.getModelState(modelState), operation.getLocation())
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new Package node");
                  }
               });
            break;
         }
         case Types.COMPONENT: {
            modelAccess.addComponent(UmlModelState.getModelState(modelState), operation.getLocation())
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new Component node");
                  }
               });
            break;
         }
         case Types.ACTOR: {
            PackageableElement container = null;
            try {
               container = getOrThrow(
                  UmlModelState.getModelState(modelState).getIndex().getSemantic(operation.getContainerId()),
                  PackageableElement.class, "No valid container with id " + operation.getContainerId() + " found");
            } catch (GLSPServerException ex) {
               LOGGER.error("Could not find container", ex);
            }
            if (container != null && container instanceof org.eclipse.uml2.uml.internal.impl.ModelImpl) {
               modelAccess.addActor(UmlModelState.getModelState(modelState), operation.getLocation())
                  .thenAccept(response -> {
                     if (!response.body()) {
                        throw new GLSPServerException("Could not execute create operation on new Actor node");
                     }
                  });
            } else {
               modelAccess
                  .addActorInPackage(UmlModelState.getModelState(modelState), (Package) container,
                     operation.getLocation())
                  .thenAccept(response -> {
                     if (!response.body()) {
                        throw new GLSPServerException("Could not execute create operation on new nested Actor node");
                     }
                  });
            }

            /* WHY ALSO TRUE FOR ModelImpl??? if (container != null && container instanceof Package) */
            break;
         }
         case Types.USECASE: {
            PackageableElement container = null;
            try {
               container = getOrThrow(
                  UmlModelState.getModelState(modelState).getIndex().getSemantic(operation.getContainerId()),
                  PackageableElement.class, "No valid container with id " + operation.getContainerId() + " found");
            } catch (GLSPServerException ex) {
               LOGGER.error("Could not find container", ex);
            }
            if (container != null && container instanceof org.eclipse.uml2.uml.internal.impl.ModelImpl) {
               modelAccess.addUsecase(UmlModelState.getModelState(modelState), operation.getLocation())
                  .thenAccept(response -> {
                     if (!response.body()) {
                        throw new GLSPServerException("Could not execute create operation on new Usecase node");
                     }
                  });
            } else if (container instanceof Package) {
               modelAccess
                  .addUsecaseInPackage(UmlModelState.getModelState(modelState), (Package) container,
                     operation.getLocation())
                  .thenAccept(response -> {
                     if (!response.body()) {
                        throw new GLSPServerException("Could not execute create operation on new nested Usecase node");
                     }
                  });
            }
            break;
         }
         case Types.COMMENT: {
            modelAccess.addComment(UmlModelState.getModelState(modelState), operation.getLocation())
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new Comment node");
                  }
               });
            break;
         }
      }
   }

   @Override
   public String getLabel() { return "Create uml classifier"; }

}
