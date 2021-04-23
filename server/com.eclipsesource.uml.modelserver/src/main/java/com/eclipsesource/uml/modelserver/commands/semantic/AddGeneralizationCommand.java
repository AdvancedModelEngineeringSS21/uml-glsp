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
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddGeneralizationCommand extends UmlSemanticElementCommand {

   private final Generalization newGeneralization;
   protected final Classifier generalClassifier;
   protected final Classifier specificClassifier;

   public AddGeneralizationCommand(final EditingDomain domain, final URI modelUri,
      final String generalClassifierUri, final String specificClassifierUri) {
      super(domain, modelUri);
      this.newGeneralization = UMLFactory.eINSTANCE.createGeneralization();
      this.generalClassifier = UmlSemanticCommandUtil.getElement(umlModel, generalClassifierUri, Classifier.class);
      this.specificClassifier = UmlSemanticCommandUtil.getElement(umlModel, specificClassifierUri, Classifier.class);
   }

   @Override
   protected void doExecute() {

      generalClassifier.getGeneralizations().add(getNewGeneralization());
      getNewGeneralization().setGeneral(generalClassifier);
      getNewGeneralization().setSpecific(specificClassifier);

   }

   public Generalization getNewGeneralization() { return newGeneralization; }

}
