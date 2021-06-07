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

import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.graph.util.GraphUtil;

public class UmlPositioningAndSizingUtil {
   private UmlPositioningAndSizingUtil() {}

   public static int HEADER_HEIGHT = 38;

   public static GPoint getRelativePosition(final GPoint parentPosition, final GPoint clickLocation) {
      return getRelativePosition(parentPosition, clickLocation, true);
   }

   public static GPoint getRelativePosition(final GPoint parentPosition, final GPoint clickLocation,
      final boolean parentHasHeader) {
      if (parentHasHeader) {
         return GraphUtil.point(clickLocation.getX() - parentPosition.getX(),
            clickLocation.getY() - parentPosition.getY() - HEADER_HEIGHT);
      } else {
         return GraphUtil.point(clickLocation.getX() - parentPosition.getX(),
            clickLocation.getY() - parentPosition.getY());
      }
   }
}
