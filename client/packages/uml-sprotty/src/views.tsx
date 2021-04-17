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
import {
    CircularNodeView,
    getSubType,
    IView,
    RectangularNodeView,
    RenderingContext,
    setAttr,
    SLabelView
} from "sprotty/lib";

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
        console.log(`wir sind hier2 - ${node.bounds.width} - ${node.bounds.height} - ${node.bounds.x} - ${node.bounds.y}`);
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
export class UseCaseNodeView extends CircularNodeView {
    render(node: LabeledNode, context: RenderingContext): VNode {
        const rX = Math.max(node.size.width, node.size.height) / 2;
        const rY = Math.min(node.size.width, node.size.height) / 2;

        return <g>
            <ellipse class-sprotty-node={node.selected} class-sprotty-port={node.selected}
                class-mouseover={node.hoverFeedback} class-selected={node.selected}
                cx={rX} cy={rY} rx={rX} ry={rY}></ellipse>
            {context.renderChildren(node)}
        </g >;
    }
}
