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
package com.eclipsesource.uml.modelserver.commands.semantic;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddUsecaseCommand extends UmlSemanticElementCommand {

   protected final UseCase newUsecase;

   public AddUsecaseCommand(final EditingDomain domain, final URI modelUri) {
      super(domain, modelUri);
      this.newUsecase = UMLFactory.eINSTANCE.createUseCase();
   }

   /**
    * Adds a new actor to the UML model.
    */
   @Override
   protected void doExecute() {
      newUsecase.setName(UmlSemanticCommandUtil.getNewUsecaseName(umlModel));
      umlModel.getPackagedElements().add(newUsecase);
   }

   public UseCase getNewUsecase() { return newUsecase; }

}
