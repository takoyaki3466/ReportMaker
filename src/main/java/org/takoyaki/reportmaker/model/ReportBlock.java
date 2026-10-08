package org.takoyaki.reportmaker.model;

import java.io.Serializable;

public sealed interface ReportBlock extends Serializable
        permits TextBlock, SubsectionBlock, FigureBlock, EquationBlock {
    BlockType getType();
}
