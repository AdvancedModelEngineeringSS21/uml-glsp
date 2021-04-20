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
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.ExtensionPoint;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddExtendCommand extends UmlSemanticElementCommand {

   private final Extend newExtend;
   protected final UseCase extendingUsecase;
   protected final UseCase extendedUsecase;

   public AddExtendCommand(final EditingDomain domain, final URI modelUri,
      final String extendingUsecaseUri, final String extendedUsecaseUri) {
      super(domain, modelUri);
      this.newExtend = UMLFactory.eINSTANCE.createExtend();
      this.extendingUsecase = UmlSemanticCommandUtil.getElement(umlModel, extendingUsecaseUri, UseCase.class);
      this.extendedUsecase = UmlSemanticCommandUtil.getElement(umlModel, extendedUsecaseUri, UseCase.class);
   }

   @Override
   protected void doExecute() {

      extendingUsecase.getExtends().add(getNewExtend());
      ExtensionPoint newEp = UMLFactory.eINSTANCE.createExtensionPoint();
      extendedUsecase.getExtensionPoints().add(newEp);
      getNewExtend().setExtendedCase(extendedUsecase);
      getNewExtend().setExtension(extendingUsecase);
      getNewExtend().getExtensionLocations().add(newEp);
   }

   public Extend getNewExtend() { return newExtend; }

}
