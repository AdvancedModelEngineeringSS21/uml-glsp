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
/** @jsx svg */
/* eslint-disable react/jsx-key */
import { injectable } from "inversify";
import { svg } from "snabbdom-jsx";
import { VNode } from "snabbdom/vnode";
import { getSubType, IView, PolylineEdgeView, RectangularNodeView, RenderingContext, setAttr, ShapeView, SLabelView, SEdge, Point } from "sprotty/lib";

import { Icon, LabeledNode, SLabelNode } from "./model";

/** @jsx svg */
/* eslint-disable react/jsx-key */

@injectable()
export class ClassNodeView extends RectangularNodeView {
    render(node: LabeledNode, context: RenderingContext): VNode {
        const rhombStr = "M 0,38  L " + node.bounds.width + ",38";

        return <g class-node={true} class-selected={node.selected} class-mouseover={node.hoverFeedback}>
            <defs>
                <filter id="dropShadow">
                    <feDropShadow dx="1.5" dy="1.5" stdDeviation="0.5" style-flood-color="var(--uml-drop-shadow)" style-flood-opacity="0.5" />
                </filter>
            </defs>

            <rect x={0} y={0} rx={2} ry={2} width={Math.max(0, node.bounds.width)} height={Math.max(0, node.bounds.height)} />
            {context.renderChildren(node)}
            {(node.children[1] && node.children[1].children.length > 0) ?
                <path class-uml-comp-separator={true} d={rhombStr}></path> : ""}
        </g>;
    }
}

@injectable()
export class IconView implements IView {
    render(element: Icon, context: RenderingContext): VNode {
        let image;
        if (element.iconImageName) {
            image = require("../images/" + element.iconImageName);
        }

        return <g>
            <image class-sprotty-icon={true} href={image} x={-2} y={-1} width={20} height={20}></image>
            {context.renderChildren(element)}
        </g>;
    }
}

@injectable()
export class LabelNodeView extends SLabelView {
    render(labelNode: Readonly<SLabelNode>, context: RenderingContext): VNode {
        let image;
        if (labelNode.imageName) {
            image = require("../images/" + labelNode.imageName);
        }

        const vnode = (
            <g
                class-selected={labelNode.selected}
                class-mouseover={labelNode.hoverFeedback}
                class-sprotty-label-node={true}
            >
                {!!image && <image class-sprotty-icon={true} href={image} y={-8} width={22} height={15}></image>}
                <text class-sprotty-label={true} x={image ? 30 : 0}>{labelNode.text}</text>
            </g>
        );

        const subType = getSubType(labelNode);
        if (subType) {
            setAttr(vnode, "class", subType);
        }
        return vnode;
    }
}

@injectable()
export class PackageNodeView extends RectangularNodeView {
    render(node: LabeledNode, context: RenderingContext): VNode {
        const rhombStr = "M 0,38  L " + node.bounds.width + ",38";
        return <g class-node={true} class-selected={node.selected} class-mouseover={node.hoverFeedback}>
            <defs>
                <filter id="dropShadow">
                    <feDropShadow dx="1.5" dy="1.5" stdDeviation="0.5" style-flood-color="var(--uml-drop-shadow)" style-flood-opacity="0.5" />
                </filter>
            </defs>

            <rect x={0} y={0} rx={2} ry={2} width={Math.max(0, node.bounds.width)} height={Math.max(0, node.bounds.height)} />
            {context.renderChildren(node)}
            {(node.children[1] && node.children[1].children.length > 0) ?
                <path class-uml-comp-separator={true} d={rhombStr}></path> : ""}
        </g>;
    }
}

@injectable()
export class ActorNodeView extends RectangularNodeView {
    render(node: LabeledNode, context: RenderingContext): VNode {
        return <g>
            <defs>
                <filter id="dropShadow">
                    <feDropShadow dx="1.5" dy="1.5" stdDeviation="0.5" style-flood-color="var(--uml-drop-shadow)" style-flood-opacity="0.5" />
                </filter>
            </defs>
            <path d="m 91.166271,19.719835 a 13.195118,13.068849 0 1 1 -26.390236,0 13.195118,13.068849 0 1 1 26.390236,0 z" />
            <path d="m 77.497641,34.903691 0,46.056642 M 77.497641,80.392123 58.052204,96.933371 M 77.529208,80.392123 98.868681,
                95.860084 M 57.073619,47.49903 98.931815,47.46746"/>
            {context.renderChildren(node)}
        </g>;
    }
}

@injectable()
export class UseCaseNodeView extends ShapeView {
    render(node: LabeledNode, context: RenderingContext): VNode {
        const rX = ((Math.max(node.size.width, node.size.height) < 0) ? 0 : Math.max(node.size.width, node.size.height) / 2);
        const rY = ((Math.min(node.size.width, node.size.height) < 0) ? 0 : Math.min(node.size.width, node.size.height) / 2);

        return <g class-node={true} class-selected={node.selected} class-mouseover={node.hoverFeedback}>
            <defs>
                <filter id="dropShadow">
                    <feDropShadow dx="1.5" dy="1.5" stdDeviation="0.5" style-flood-color="var(--uml-drop-shadow)" style-flood-opacity="0.5" />
                </filter>
            </defs>
            <ellipse cx={rX} cy={rY} rx={rX} ry={rY} />
            {context.renderChildren(node)}
        </g >;
    }
}

@injectable()
export class DirectedEdgeView extends PolylineEdgeView {
    protected renderAdditionals(edge: SEdge, segments: Point[], context: RenderingContext): VNode[] {
        return [<defs>
            <marker id="triangle" viewBox="0 0 10 10"
                refX="1" refY="5"
                markerUnits="strokeWidth"
                markerWidth="10" markerHeight="10"
                orient="auto">
                <path d="M 0 0 L 10 5 L 0 10 z" fill="#f00" />
            </marker>
        </defs>];
    }

    protected renderLine(edge: SEdge, segments: Point[], context: RenderingContext): VNode {
        const firstPoint = segments[0];
        let path = `M ${firstPoint.x},${firstPoint.y}`;
        for (let i = 1; i < segments.length; i++) {
            const p = segments[i];
            path += ` L ${p.x},${p.y}`;
        }
        return <path d={path} marker-end="url(#triangle)" />;
    }
    render(edge: Readonly<SEdge>, context: RenderingContext): VNode | undefined {
        const router = this.edgeRouterRegistry.get(edge.routerKind);
        const route = router.route(edge);
        if (route.length === 0) {
            return this.renderDanglingEdge("Cannot compute route", edge, context);
        }
        if (!this.isVisible(edge, route, context)) {
            if (edge.children.length === 0) {
                return undefined;
            }
            // The children of an edge are not necessarily inside the bounding box of the route,
            // so we need to render a group to ensure the children have a chance to be rendered.
            return <g>{context.renderChildren(edge, { route })}</g>;
        }

        return <g class-sprotty-edge={true} class-mouseover={edge.hoverFeedback}>

            {this.renderAdditionals(edge, route, context)}
            {this.renderLine(edge, route, context)}
            {context.renderChildren(edge, { route })}
        </g>;
    }
}
