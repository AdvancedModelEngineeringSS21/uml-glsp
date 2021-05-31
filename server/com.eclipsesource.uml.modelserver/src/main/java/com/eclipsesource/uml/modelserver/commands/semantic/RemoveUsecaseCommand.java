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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class RemoveUsecaseCommand extends UmlSemanticElementCommand {

   protected final String semanticUriFragment;

   public RemoveUsecaseCommand(final EditingDomain domain, final URI modelUri, final String semanticUriFragment) {
      super(domain, modelUri);
      this.semanticUriFragment = semanticUriFragment;
   }

   @Override
   protected void doExecute() {
      UseCase useCaseToRemove = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment, UseCase.class);
      if (useCaseToRemove == null) {
         return;
      }
      EObject container = useCaseToRemove.eContainer();
      if (container == null || container instanceof org.eclipse.uml2.uml.internal.impl.ModelImpl) {
         umlModel.getPackagedElements().remove(useCaseToRemove);
      } else if (container instanceof Package) {
         ((Package) container).getPackagedElements().remove(useCaseToRemove);
      } else if (container instanceof Component) {
         ((Component) container).getPackagedElements().remove(useCaseToRemove);
      }
   }

}
