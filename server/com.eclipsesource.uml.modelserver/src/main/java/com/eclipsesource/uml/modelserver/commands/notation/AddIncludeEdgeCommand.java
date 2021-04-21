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
package com.eclipsesource.uml.modelserver.commands.notation;

import java.util.function.Supplier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Include;

import com.eclipsesource.uml.modelserver.commands.util.UmlNotationCommandUtil;
import com.eclipsesource.uml.modelserver.unotation.Edge;
import com.eclipsesource.uml.modelserver.unotation.SemanticProxy;
import com.eclipsesource.uml.modelserver.unotation.UnotationFactory;

public class AddIncludeEdgeCommand extends UmlNotationElementCommand {

   protected String semanticProxyUri;
   protected Supplier<Include> inlcudeSupplier;

   private AddIncludeEdgeCommand(final EditingDomain domain, final URI modelUri) {
      super(domain, modelUri);
      this.semanticProxyUri = null;
      this.inlcudeSupplier = null;
   }

   public AddIncludeEdgeCommand(final EditingDomain domain, final URI modelUri, final String semanticProxyUri) {
      this(domain, modelUri);
      this.semanticProxyUri = semanticProxyUri;
   }

   public AddIncludeEdgeCommand(final EditingDomain domain, final URI modelUri,
      final Supplier<Include> includeSupplier) {
      this(domain, modelUri);
      this.inlcudeSupplier = includeSupplier;
   }

   @Override
   protected void doExecute() {
      Edge newEdge = UnotationFactory.eINSTANCE.createEdge();

      SemanticProxy proxy = UnotationFactory.eINSTANCE.createSemanticProxy();
      if (this.semanticProxyUri != null) {
         proxy.setUri(this.semanticProxyUri);
      } else {
         proxy.setUri(UmlNotationCommandUtil.getSemanticProxyUri(inlcudeSupplier.get()));
      }
      newEdge.setSemanticElement(proxy);

      umlDiagram.getElements().add(newEdge);
   }
}
