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
package com.eclipsesource.uml.glsp.modelserver;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.client.ModelServerClientApi;
import org.eclipse.emfcloud.modelserver.client.NotificationSubscriptionListener;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.graph.impl.GEdgeImpl;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.glsp.server.protocol.GLSPServerException;
import org.eclipse.glsp.server.types.ElementAndBounds;
import org.eclipse.glsp.server.types.ElementAndRoutingPoints;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.ExtensionPoint;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Include;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UseCase;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.modelserver.UmlModelServerClient;
import com.eclipsesource.uml.modelserver.UmlNotationUtil;
import com.eclipsesource.uml.modelserver.commands.contributions.AddActorCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddAssociationCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddClassCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddCommentCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddCommentEdgeCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddComponentCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddExtendCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddGeneralizationCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddIncludeCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddPackageCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddPropertyCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddUsecaseCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.ChangeBoundsCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.ChangeRoutingPointsCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveActorCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveAssociationCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveClassCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveCommentCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveCommentEdgeCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveComponentCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveExtendCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveExtensionPointCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveGeneralizationCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveIncludeCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemovePackageCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemovePropertyCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveUsecaseCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetActorNameCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetAssociationEndMultiplicityCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetAssociationEndNameCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetClassNameCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetCommentBodyCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetExtensionPointNameCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetPackageNameCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetPropertyCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetUsecaseNameCommandContribution;
import com.eclipsesource.uml.modelserver.unotation.Edge;
import com.eclipsesource.uml.modelserver.unotation.Shape;
import com.google.common.base.Preconditions;

/**
 * The UmlModelServerAccess maps operations performed on the client, e.g. Creating Nodes and Edges
 * to the corresponding Commands on the model server.
 * One important thing to note is that the CommandContributions used here MUST be added on the model server in the
 * UmlModelServerModule via bindings.put(...) in order for the calls to work.
 *
 */
public class UmlModelServerAccess {

   private static Logger LOGGER = Logger.getLogger(UmlModelServerAccess.class);

   private static final String FORMAT_XMI = "xmi";

   private final URI baseSourceUri;

   private final UmlModelServerClient modelServerClient;
   private NotificationSubscriptionListener<EObject> subscriptionListener;

   public UmlModelServerAccess(final String sourceURI, final UmlModelServerClient modelServerClient) {
      Preconditions.checkNotNull(modelServerClient);
      this.baseSourceUri = URI.createURI(sourceURI, true).trimFileExtension();
      this.modelServerClient = modelServerClient;
   }

   public String getSemanticURI() { return baseSourceUri.appendFileExtension(UMLResource.FILE_EXTENSION).toString(); }

   public String getNotationURI() {
      return baseSourceUri.appendFileExtension(UmlNotationUtil.NOTATION_EXTENSION).toString();
   }

   public ModelServerClientApi<EObject> getModelServerClient() { return modelServerClient; }

   public EObject getModel() {
      try {
         return modelServerClient.get(getSemanticURI(), UMLResource.FILE_EXTENSION).thenApply(res -> res.body()).get();
      } catch (InterruptedException | ExecutionException e) {
         LOGGER.error(e);
         throw new GLSPServerException("Error during model loading", e);
      }
   }

   public EObject getNotationModel() {
      try {
         return modelServerClient.get(getNotationURI(), FORMAT_XMI).thenApply(res -> res.body()).get();
      } catch (InterruptedException | ExecutionException e) {
         LOGGER.error(e);
         throw new GLSPServerException("Error during model loading", e);
      }
   }

   public void subscribe(final NotificationSubscriptionListener<EObject> subscriptionListener) {
      LOGGER.debug("UmlModelServerAccess - subscribe");
      this.subscriptionListener = subscriptionListener;
      this.modelServerClient.subscribe(getSemanticURI(), subscriptionListener, FORMAT_XMI);
   }

   public void unsubscribe() {
      LOGGER.debug("UmlModelServerAccess - unsubscribe");
      if (subscriptionListener != null) {
         this.modelServerClient.unsubscribe(getSemanticURI());
      }
   }

   protected String getSemanticUriFragment(final EObject element) {
      return EcoreUtil.getURI(element).fragment();
   }

   /*
    * UML Types
    */
   public CompletableFuture<Response<List<String>>> getUmlTypes() {
      return this.modelServerClient.getUmlTypes(getSemanticURI());
   }

   /*
    * UML Class
    */
   public CompletableFuture<Response<Boolean>> addClass(final UmlModelState modelState,
      final Optional<GPoint> newPosition) {

      CCompoundCommand addClassCompoundCommand = AddClassCommandContribution
         .create(newPosition.orElse(GraphUtil.point(0, 0)));
      return this.edit(addClassCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeClass(final UmlModelState modelState,
      final Class classToRemove) {

      String semanticProxyUri = getSemanticUriFragment(classToRemove);
      CCompoundCommand compoundCommand = RemoveClassCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   public CompletableFuture<Response<Boolean>> setClassName(final UmlModelState modelState,
      final Class classToRename, final String newName) {

      CCommand setClassNameCommand = SetClassNameCommandContribution.create(getSemanticUriFragment(classToRename),
         newName);
      return this.edit(setClassNameCommand);
   }

   /*
    * UML Property
    */
   public CompletableFuture<Response<Boolean>> addProperty(final UmlModelState modelState,
      final Class parentClass) {

      CCommand addPropertyCommand = AddPropertyCommandContribution.create(getSemanticUriFragment(parentClass));
      return this.edit(addPropertyCommand);
   }

   public CompletableFuture<Response<Boolean>> removeProperty(final UmlModelState modelState,
      final Property propertyToRemove) {

      Class parentClass = (Class) propertyToRemove.eContainer();
      CCommand removePropertyCommand = RemovePropertyCommandContribution
         .create(getSemanticUriFragment(parentClass), getSemanticUriFragment(propertyToRemove));
      return this.edit(removePropertyCommand);
   }

   public CompletableFuture<Response<Boolean>> setProperty(final UmlModelState modelState,
      final Property propertyToRename, final String newName, final String newType, final String newBounds) {

      CCommand setPropertyNameCommand = SetPropertyCommandContribution
         .create(getSemanticUriFragment(propertyToRename), newName, newType, newBounds);
      return this.edit(setPropertyNameCommand);
   }

   /*
    * UML Association
    */
   public CompletableFuture<Response<Boolean>> addAssociation(final UmlModelState modelState,
      final Class sourceClass, final Class targetClass) {

      CCompoundCommand addAssociationCompoundCommand = AddAssociationCommandContribution
         .create(getSemanticUriFragment(sourceClass), getSemanticUriFragment(targetClass));
      return this.edit(addAssociationCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeAssociation(final UmlModelState modelState,
      final Association associationToRemove) {

      String semanticProxyUri = getSemanticUriFragment(associationToRemove);
      CCompoundCommand compoundCommand = RemoveAssociationCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   public CompletableFuture<Response<Boolean>> setAssociationEndName(final UmlModelState modelState,
      final Property associationEnd, final String newName) {

      CCommand setClassNameCommand = SetAssociationEndNameCommandContribution.create(
         getSemanticUriFragment(associationEnd), newName);
      return this.edit(setClassNameCommand);
   }

   public CompletableFuture<Response<Boolean>> setAssociationEndMultiplicity(final UmlModelState modelState,
      final Property associationEnd, final String newBounds) {

      CCommand setClassNameCommand = SetAssociationEndMultiplicityCommandContribution.create(
         getSemanticUriFragment(associationEnd), newBounds);
      return this.edit(setClassNameCommand);
   }

   /*
    * UML USE CASE DIAGRAM
    */

   /*
    * Package
    */
   /**
    * This method is called when in the frontend tool palette a new package is created.
    *
    * @param modelState
    * @param newPosition
    * @return
    */
   public CompletableFuture<Response<Boolean>> addPackage(final UmlModelState modelState,
      final Optional<GPoint> newPosition) {

      CCompoundCommand addPackageCompoundCommand = AddPackageCommandContribution
         .create(newPosition.orElse(GraphUtil.point(0, 0)));
      return this.edit(addPackageCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removePackage(final UmlModelState modelState,
      final Package packageToRemove) {
      String semanticProxyUri = getSemanticUriFragment(packageToRemove);
      CCompoundCommand compoundCommand = RemovePackageCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   public CompletableFuture<Response<Boolean>> setPackageName(final UmlModelState modelState,
      final Package packageToRename, final String newName) {
      CCommand setPackageNameCommand = SetPackageNameCommandContribution.create(getSemanticUriFragment(packageToRename),
         newName);
      return this.edit(setPackageNameCommand);
   }

   /*
    * Component
    */
   /**
    * This method is called when in the frontend tool palette a new component is created.
    *
    * @param modelState
    * @param newPosition
    * @return
    */
   public CompletableFuture<Response<Boolean>> addComponent(final UmlModelState modelState,
      final Optional<GPoint> newPosition) {

      CCompoundCommand addComponentCompoundCommand = AddComponentCommandContribution
         .create(newPosition.orElse(GraphUtil.point(0, 0)));
      return this.edit(addComponentCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> addComponentInPackage(final UmlModelState modelState,
      final Package parent,
      final Optional<GPoint> newPosition) {

      CCompoundCommand addComponentCompoundCommand = AddComponentCommandContribution
         .create(newPosition.orElse(GraphUtil.point(0, 0)), getSemanticUriFragment(parent));
      return this.edit(addComponentCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeComponent(final UmlModelState modelState,
      final Component componentToRemove) {
      String semanticProxyUri = getSemanticUriFragment(componentToRemove);
      CCompoundCommand compoundCommand = RemoveComponentCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }
   // public CompletableFuture<Response<Boolean>> setPackageName(final UmlModelState modelState,
   // final Package packageToRename, final String newName) {
   // CCommand setPackageNameCommand = SetPackageNameCommandContribution.create(getSemanticUriFragment(packageToRename),
   // newName);
   // return this.edit(setPackageNameCommand);
   // }

   /*
    * ACTOR
    */

   /**
    * This method is called when in the frontend tool palette a new actor is created.
    *
    * @param modelState
    * @param newPosition
    * @return
    */
   public CompletableFuture<Response<Boolean>> addActor(final UmlModelState modelState,
      final Optional<GPoint> newPosition) {
      CCompoundCommand addActorCompoundCommand = AddActorCommandContribution
         .create(newPosition.orElse(GraphUtil.point(0, 0)));
      return this.edit(addActorCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> addActorInPackage(final UmlModelState modelState,
      final Package parent, final Optional<GPoint> newPosition) {
      CCommand addActorCompoundCommand = AddActorCommandContribution.create(newPosition.orElse(GraphUtil.point(0, 0)),
         getSemanticUriFragment(parent));
      return this.edit(addActorCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeActor(final UmlModelState modelState,
      final Actor actorToRemove) {

      String semanticProxyUri = getSemanticUriFragment(actorToRemove);
      CCompoundCommand compoundCommand = RemoveActorCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   public CompletableFuture<Response<Boolean>> setActorName(final UmlModelState modelState,
      final Actor actorToRename, final String newName) {

      CCommand setActorNameCommand = SetActorNameCommandContribution.create(getSemanticUriFragment(actorToRename),
         newName);
      return this.edit(setActorNameCommand);
   }

   /*
    * USECASE
    */

   /**
    * This method is called when in the frontend tool palette a new use case is created.
    *
    * @param modelState
    * @param newPosition
    * @return
    */
   public CompletableFuture<Response<Boolean>> addUsecase(final UmlModelState modelState,
      final Optional<GPoint> newPosition) {

      CCompoundCommand addUsecaseCompoundCommand = AddUsecaseCommandContribution
         .create(newPosition.orElse(GraphUtil.point(0, 0)));
      return this.edit(addUsecaseCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> addUsecaseInParent(final UmlModelState modelState,
      final EObject parent, final Optional<GPoint> newPosition) throws Exception {
      if (!(parent instanceof Package || parent instanceof Component)) {
         throw new Exception("Element not valid as a parent for usecase");
      }
      CCommand addUseCaseCompoundCommand = AddUsecaseCommandContribution.create(
         newPosition.orElse(GraphUtil.point(0, 0)),
         getSemanticUriFragment(parent));
      return this.edit(addUseCaseCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> addUsecaseInComponent(final UmlModelState modelState,
      final Component parent, final Optional<GPoint> newPosition) {
      CCommand addUseCaseCompoundCommand = AddUsecaseCommandContribution.create(
         newPosition.orElse(GraphUtil.point(0, 0)),
         getSemanticUriFragment(parent));
      return this.edit(addUseCaseCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeUsecase(final UmlModelState modelState,
      final UseCase usecaseToRemove) {

      String semanticProxyUri = getSemanticUriFragment(usecaseToRemove);
      CCompoundCommand compoundCommand = RemoveUsecaseCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   public CompletableFuture<Response<Boolean>> setUsecaseName(final UmlModelState modelState,
      final UseCase useCaseToRename, final String newName) {

      CCommand setUsecaseNameCommand = SetUsecaseNameCommandContribution.create(getSemanticUriFragment(useCaseToRename),
         newName);
      return this.edit(setUsecaseNameCommand);
   }

   public CompletableFuture<Response<Boolean>> removeExtensionPoint(final UmlModelState modelState,
      final ExtensionPoint epToRemove) {

      String semanticProxyUri = getSemanticUriFragment(epToRemove);
      CCompoundCommand compoundCommand = RemoveExtensionPointCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   public CompletableFuture<Response<Boolean>> setExtensionPointName(final UmlModelState modelState,
      final ExtensionPoint epToRename, final String newName) {

      CCommand setExtensionPointNameCommand = SetExtensionPointNameCommandContribution.create(
         getSemanticUriFragment(epToRename),
         newName);
      return this.edit(setExtensionPointNameCommand);
   }

   /*
    * COMMENT
    */

   public CompletableFuture<Response<Boolean>> addComment(final UmlModelState modelState,
      final Optional<GPoint> newPosition) {

      CCompoundCommand addCommentCompoundCommand = AddCommentCommandContribution
         .create(newPosition.orElse(GraphUtil.point(0, 0)));
      return this.edit(addCommentCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> addCommentToElement(final UmlModelState modelState,
      final Element annotatedElement, final Optional<GPoint> newPosition) {
      CCommand addCommentCompoundCommand = AddCommentCommandContribution.create(
         newPosition.orElse(GraphUtil.point(0, 0)),
         getSemanticUriFragment(annotatedElement));
      return this.edit(addCommentCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeComment(final UmlModelState modelState,
      final Comment commentToRemove) {

      String semanticProxyUri = getSemanticUriFragment(commentToRemove);
      CCompoundCommand compoundCommand = RemoveCommentCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
      // TODO: Remove also the comment_association_edge
   }

   public CompletableFuture<Response<Boolean>> setCommentBody(final UmlModelState modelState,
      final Comment comment, final String newBody) {

      CCommand setCommentBodyCommandContribution = SetCommentBodyCommandContribution.create(
         getSemanticUriFragment(comment),
         newBody);
      return this.edit(setCommentBodyCommandContribution);
   }

   public CompletableFuture<Response<Boolean>> addCommentEdge(final UmlModelState modelState,
      final Comment sourceClass, final EObject targetClass) {

      CCompoundCommand addCommentEdgeCompoundCommand = AddCommentEdgeCommandContribution
         .create(getSemanticUriFragment(sourceClass), getSemanticUriFragment(targetClass));
      return this.edit(addCommentEdgeCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeCommentEdge(final UmlModelState modelState,
      final String commentEdgeUri) {
      GModelRoot model = modelState.getRoot();
      TreeIterator iterator = model.eAllContents();
      while (iterator.hasNext()) {
         Object next = iterator.next();
         if (next instanceof GEdgeImpl) {
            GEdgeImpl cur = ((GEdgeImpl) next);
            if (cur.getId().equals(commentEdgeUri)) {
               GModelElement parent = cur.getParent();
               String targetId = cur.getTargetId() != parent.getId() ? cur.getTargetId() : cur.getSourceId();
               System.out.println("Parent " + parent.getId());
               System.out.println("Target " + targetId);
               CCompoundCommand removeCommentEdgeCompoundCommand = RemoveCommentEdgeCommandContribution
                  .create(parent.getId().toString(), targetId.toString());
               return this.edit(removeCommentEdgeCompoundCommand);
            }
         }
      }
      return null;
   }

   /*
    * Use Case Diagram Association
    */
   public CompletableFuture<Response<Boolean>> addAssociation(final UmlModelState modelState,
      final Classifier sourceClassifier, final Classifier targetClassifier) {

      CCompoundCommand addAssociationCompoundCommand = AddAssociationCommandContribution
         .create(getSemanticUriFragment(sourceClassifier), getSemanticUriFragment(targetClassifier));
      return this.edit(addAssociationCompoundCommand);
   }

   // Removing and changing multiplicity is unchanged from UML Class diagram, naming is not relevant

   /*
    * Use Case Diagram Extend
    */
   public CompletableFuture<Response<Boolean>> addExtend(final UmlModelState modelState,
      final UseCase extendingUseCase, final UseCase extendedUseCase) {

      CCompoundCommand addExtensionCompoundCommand = AddExtendCommandContribution
         .create(getSemanticUriFragment(extendingUseCase), getSemanticUriFragment(extendedUseCase));
      return this.edit(addExtensionCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> addExtend(final UmlModelState modelState,
      final UseCase extendingUseCase, final ExtensionPoint extendedUseCase) {

      CCompoundCommand addExtensionCompoundCommand = AddExtendCommandContribution
         .create(getSemanticUriFragment(extendingUseCase), getSemanticUriFragment(extendedUseCase));
      return this.edit(addExtensionCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeExtend(final UmlModelState modelState,
      final Extend extendToRemove) {

      String semanticProxyUri = getSemanticUriFragment(extendToRemove);
      CCompoundCommand compoundCommand = RemoveExtendCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   /*
    * Use Case Diagram Include
    */
   public CompletableFuture<Response<Boolean>> addInclude(final UmlModelState modelState,
      final UseCase includingUseCase, final UseCase includedUseCase) {

      CCompoundCommand addIncludeCompoundCommand = AddIncludeCommandContribution
         .create(getSemanticUriFragment(includingUseCase), getSemanticUriFragment(includedUseCase));
      return this.edit(addIncludeCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeInclude(final UmlModelState modelState,
      final Include includeToRemove) {

      String semanticProxyUri = getSemanticUriFragment(includeToRemove);
      CCompoundCommand compoundCommand = RemoveIncludeCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   /*
    * Use Case Diagram Generalization
    */
   public CompletableFuture<Response<Boolean>> addGeneralization(final UmlModelState modelState,
      final Classifier generalClassifier, final Classifier specificClassifier) {

      CCompoundCommand addGeneralizationCompoundCommand = AddGeneralizationCommandContribution
         .create(getSemanticUriFragment(generalClassifier), getSemanticUriFragment(specificClassifier));
      return this.edit(addGeneralizationCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeGeneralization(final UmlModelState modelState,
      final Generalization generalizationToRemove) {

      String semanticProxyUri = getSemanticUriFragment(generalizationToRemove);
      CCompoundCommand compoundCommand = RemoveGeneralizationCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   /*
    * END UML USE CASE DIAGRAM
    */

   /*
    * Change Bounds
    */
   public CompletableFuture<Response<Boolean>> changeBounds(final Map<Shape, ElementAndBounds> changeBoundsMap) {
      CCompoundCommand compoundCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      compoundCommand.setType(ChangeBoundsCommandContribution.TYPE);
      changeBoundsMap.forEach((shape, elementAndBounds) -> {
         CCommand changeBoundsCommand = ChangeBoundsCommandContribution.create(shape.getSemanticElement().getUri(),
            elementAndBounds.getNewPosition(), elementAndBounds.getNewSize());
         compoundCommand.getCommands().add(changeBoundsCommand);
      });
      return this.edit(compoundCommand);
   }

   /*
    * Change Routing Points
    */
   public CompletableFuture<Response<Boolean>> changeRoutingPoints(
      final Map<Edge, ElementAndRoutingPoints> changeBendPointsMap) {
      CCompoundCommand compoundCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      compoundCommand.setType(ChangeRoutingPointsCommandContribution.TYPE);

      changeBendPointsMap.forEach((edge, elementAndRoutingPoints) -> {
         CCommand changeRoutingPointsCommand = ChangeRoutingPointsCommandContribution.create(
            edge.getSemanticElement().getUri(), elementAndRoutingPoints.getNewRoutingPoints());
         compoundCommand.getCommands().add(changeRoutingPointsCommand);
      });
      return this.edit(compoundCommand);
   }

   protected CompletableFuture<Response<Boolean>> edit(final CCommand command) {
      return this.modelServerClient.edit(getSemanticURI(), command, FORMAT_XMI);
   }

   public boolean save() {
      try {
         return this.modelServerClient.save(getSemanticURI()).thenApply(res -> res.body()).get();
      } catch (InterruptedException | ExecutionException e) {
         return false;
      }
   }

   public boolean undo() {
      try {
         return this.modelServerClient.undo(getSemanticURI()).thenApply(res -> res.body()).get();
      } catch (InterruptedException | ExecutionException e) {
         return false;
      }
   }

   public boolean redo() {
      try {
         return this.modelServerClient.redo(getSemanticURI()).thenApply(res -> res.body()).get();
      } catch (InterruptedException | ExecutionException e) {
         return false;
      }
   }

}
