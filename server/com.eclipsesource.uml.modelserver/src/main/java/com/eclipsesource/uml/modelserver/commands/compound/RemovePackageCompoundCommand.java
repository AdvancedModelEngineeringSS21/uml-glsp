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
package com.eclipsesource.uml.modelserver.commands.compound;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.modelserver.commands.notation.RemovePackageShapeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveCommentEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemovePackageCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlCommentEdgeRemoveUtil;
import com.eclipsesource.uml.modelserver.commands.util.UmlNotationCommandUtil;
import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class RemovePackageCompoundCommand extends CompoundCommand {

   public RemovePackageCompoundCommand(final EditingDomain domain, final URI modelUri,
      final String semanticUriFragment) {

      Model umlModel = UmlSemanticCommandUtil.getModel(modelUri, domain);
      Package packageToRemove = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment, Package.class);

      for (RemoveCommentEdgeCommand c : UmlCommentEdgeRemoveUtil.removeIncomingCommentEdge(modelUri, domain,
         semanticUriFragment)) {
         this.append(c);
      }

      for (PackageableElement elem : packageToRemove.getPackagedElements()) {
         if (elem instanceof Actor) {
            String uri = UmlNotationCommandUtil.getSemanticProxyUri(elem);
            this.append(new RemoveActorCompoundCommand(domain, modelUri, uri));
         } else if (elem instanceof UseCase) {
            String uri = UmlNotationCommandUtil.getSemanticProxyUri(elem);
            this.append(new RemoveUsecaseCompoundCommand(domain, modelUri, uri));
         } else if (elem instanceof Component) {
            String uri = UmlNotationCommandUtil.getSemanticProxyUri(elem);
            this.append(new RemoveComponentCompoundCommand(domain, modelUri, uri));
         }
      }

      this.append(new RemovePackageCommand(domain, modelUri, semanticUriFragment));
      this.append(new RemovePackageShapeCommand(domain, modelUri, semanticUriFragment));
   }
}
