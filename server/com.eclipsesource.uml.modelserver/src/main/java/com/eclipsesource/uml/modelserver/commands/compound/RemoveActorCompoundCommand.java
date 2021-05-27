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

import java.util.Collection;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil.UsageCrossReferencer;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.modelserver.commands.notation.RemoveActorShapeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveActorCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveCommentEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.SetPropertyTypeCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlCommentEdgeRemoveUtil;
import com.eclipsesource.uml.modelserver.commands.util.UmlNotationCommandUtil;
import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class RemoveActorCompoundCommand extends CompoundCommand {

   public RemoveActorCompoundCommand(final EditingDomain domain, final URI modelUri, final String semanticUriFragment) {
      Model umlModel = UmlSemanticCommandUtil.getModel(modelUri, domain);
      Actor actorToRemove = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment, Actor.class);

      for (RemoveCommentEdgeCommand c : UmlCommentEdgeRemoveUtil.removeIncomingCommentEdge(modelUri, domain,
         semanticUriFragment)) {
         this.append(c);
      }
      Collection<Setting> usagesClass = UsageCrossReferencer.find(actorToRemove, umlModel.eResource());
      for (Setting setting : usagesClass) {
         EObject eObject = setting.getEObject();
         if (isPropertyTypeUsage(setting, eObject, actorToRemove)) {
            String propertyUriFragment = UmlSemanticCommandUtil.getSemanticUriFragment((Property) eObject);
            this.append(new SetPropertyTypeCommand(domain, modelUri, propertyUriFragment, null));
         } else if (isAssociationTypeUsage(setting, eObject)) {
            String associationUriFragment = UmlNotationCommandUtil
               .getSemanticProxyUri((Relationship) eObject.eContainer());
            this.append(new RemoveAssociationCompoundCommand(domain, modelUri, associationUriFragment));
         } else if (isGeneralizationTypeUsage(setting, eObject)) {
            String extendUriFragment = UmlSemanticCommandUtil
               .getSemanticUriFragment((Relationship) eObject);
            this.append(new RemoveGeneralizationCompoundCommand(domain, modelUri, extendUriFragment));
         }
      }
      this.append(new RemoveActorCommand(domain, modelUri, semanticUriFragment));
      this.append(new RemoveActorShapeCommand(domain, modelUri, semanticUriFragment));

   }

   protected boolean isPropertyTypeUsage(final Setting setting, final EObject eObject, final Actor classToRemove) {
      return eObject instanceof Property
         && eObject.eContainer() instanceof Class
         && setting.getEStructuralFeature().equals(UMLPackage.Literals.TYPED_ELEMENT__TYPE)
         && classToRemove.equals(((Property) eObject).getType());
   }

   protected boolean isAssociationTypeUsage(final Setting setting, final EObject eObject) {
      return eObject instanceof Property
         && eObject.eContainer() instanceof Association
         && ((Property) eObject).getAssociation() != null;
   }

   protected boolean isGeneralizationTypeUsage(final Setting setting, final EObject eObject) {
      return eObject instanceof Generalization
         && ((Generalization) eObject).eContainer() instanceof UseCase
         && ((Generalization) eObject).getSpecific() != null;
   }

}
