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
import org.eclipse.uml2.uml.Include;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddIncludeCommand extends UmlSemanticElementCommand {

   private final Include newInclude;
   protected final UseCase includingUsecase;
   protected final UseCase includedUsecase;

   public AddIncludeCommand(final EditingDomain domain, final URI modelUri,
      final String includingUsecaseUri, final String includedUsecaseUri) {
      super(domain, modelUri);
      this.newInclude = UMLFactory.eINSTANCE.createInclude();
      this.includingUsecase = UmlSemanticCommandUtil.getElement(umlModel, includingUsecaseUri, UseCase.class);
      this.includedUsecase = UmlSemanticCommandUtil.getElement(umlModel, includedUsecaseUri, UseCase.class);
   }

   @Override
   protected void doExecute() {

      includingUsecase.getIncludes().add(getNewInclude());
      getNewInclude().setAddition(includedUsecase);
      getNewInclude().setIncludingCase(includingUsecase);
   }

   public Include getNewInclude() { return newInclude; }
}
