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
package com.eclipsesource.uml.modelserver.commands.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Model;

import com.eclipsesource.uml.modelserver.commands.semantic.RemoveCommentEdgeCommand;

public final class UmlCommentEdgeRemoveUtil {
   private UmlCommentEdgeRemoveUtil() {}

   public static List<RemoveCommentEdgeCommand> removeIncomingCommentEdge(final URI modelUri,
      final EditingDomain domain,
      final String semanticUri) {
      List<RemoveCommentEdgeCommand> res = new ArrayList<>();
      Model umlModel = UmlSemanticCommandUtil.getModel(modelUri, domain);
      for (Comment c : umlModel.getOwnedComments()) {
         if (c.getAnnotatedElements().size() > 0 && UmlSemanticCommandUtil
            .getSemanticUriFragment(c.getAnnotatedElements().get(0)).equals(semanticUri)) {
            res.add(
               new RemoveCommentEdgeCommand(domain, modelUri, UmlSemanticCommandUtil.getSemanticUriFragment(c)));
         }
      }
      return res;
   }
}
