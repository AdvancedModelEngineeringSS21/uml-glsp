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
import org.eclipse.uml2.uml.Actor;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class SetActorNameCommand extends UmlSemanticElementCommand {

   protected String semanticUriFragment;
   protected String newName;

   public SetActorNameCommand(final EditingDomain domain, final URI modelUri, final String semanticUriFragment,
      final String newName) {
      super(domain, modelUri);
      this.semanticUriFragment = semanticUriFragment;
      this.newName = newName;
   }

   @Override
   protected void doExecute() {
      Actor actorToRename = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment, Actor.class);
      actorToRename.setName(newName);
   }

}
