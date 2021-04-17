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

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddActorCommand extends UmlSemanticElementCommand {

   private static Logger LOGGER = Logger.getLogger(AddActorCommand.class);

   protected final Actor newActor;
   protected final String parentSemanticUriFragment;

   public AddActorCommand(final EditingDomain domain, final URI modelUri) {
      super(domain, modelUri);
      this.newActor = UMLFactory.eINSTANCE.createActor();
      this.parentSemanticUriFragment = null;
   }

   public AddActorCommand(final EditingDomain domain, final URI modelUri, final String parentSemanticUri) {
      super(domain, modelUri);
      this.newActor = UMLFactory.eINSTANCE.createActor();
      this.parentSemanticUriFragment = parentSemanticUri;
   }

   /**
    * Adds a new actor to the UML model.
    */
   @Override
   protected void doExecute() {
      newActor.setName(UmlSemanticCommandUtil.getNewActorName(umlModel));
      if (parentSemanticUriFragment == null) {
         umlModel.getPackagedElements().add(newActor);
      } else {
         Package parentPackage = UmlSemanticCommandUtil.getElement(umlModel, parentSemanticUriFragment, Package.class);
         parentPackage.getPackagedElements().add(newActor);
      }
   }

   public Actor getNewActor() { return newActor; }

}
