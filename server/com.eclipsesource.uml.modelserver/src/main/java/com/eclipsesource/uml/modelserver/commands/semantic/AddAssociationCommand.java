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
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddAssociationCommand extends UmlSemanticElementCommand {

   private final Association newAssociation;
   protected final Classifier sourceClass;
   protected final Classifier targetClass;

   public AddAssociationCommand(final EditingDomain domain, final URI modelUri,
      final String sourceClassUriFragment, final String targetClassUriFragment) {
      super(domain, modelUri);
      this.newAssociation = UMLFactory.eINSTANCE.createAssociation();
      this.sourceClass = UmlSemanticCommandUtil.getElement(umlModel, sourceClassUriFragment, Classifier.class);
      this.targetClass = UmlSemanticCommandUtil.getElement(umlModel, targetClassUriFragment, Classifier.class);
   }

   @Override
   protected void doExecute() {
      if (sourceClass instanceof Class && targetClass instanceof Class) {
         getNewAssociation().createOwnedEnd(UmlSemanticCommandUtil.getNewAssociationEndName((Class) sourceClass),
            sourceClass);
         getNewAssociation().createOwnedEnd(UmlSemanticCommandUtil.getNewAssociationEndName((Class) targetClass),
            targetClass);
      } else {
         getNewAssociation().createOwnedEnd(sourceClass.getName().toLowerCase(), sourceClass);
         getNewAssociation().createOwnedEnd(targetClass.getName().toLowerCase(), targetClass);
      }

      umlModel.getPackagedElements().add(getNewAssociation());
   }

   public Association getNewAssociation() { return newAssociation; }

}
