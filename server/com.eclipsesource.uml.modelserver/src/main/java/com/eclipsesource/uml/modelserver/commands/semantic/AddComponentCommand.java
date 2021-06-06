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
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddComponentCommand extends UmlSemanticElementCommand {

   protected final Component newComponent;
   protected final String parentSemanticUriFragment;

   public AddComponentCommand(final EditingDomain domain, final URI modelUri) {
      super(domain, modelUri);
      this.newComponent = UMLFactory.eINSTANCE.createComponent();
      this.parentSemanticUriFragment = null;
   }

   public AddComponentCommand(final EditingDomain domain, final URI modelUri, final String parentUri) {
      super(domain, modelUri);
      this.newComponent = UMLFactory.eINSTANCE.createComponent();
      this.parentSemanticUriFragment = parentUri;
   }

   @Override
   protected void doExecute() {
      newComponent.setName(UmlSemanticCommandUtil.getNewComponentName(umlModel));
      if (parentSemanticUriFragment == null) {
         umlModel.getPackagedElements().add(newComponent);
      } else {
         EObject parent = UmlSemanticCommandUtil.getElement(umlModel, parentSemanticUriFragment);
         if (parent instanceof Package) {
            ((Package) parent).getPackagedElements().add(newComponent);
         }
      }
   }

   public Component getNewComponent() { return newComponent; }

}
