package org.takoyaki.reportmaker.model;

import java.util.UUID;

public final class FigureBlock implements ReportBlock {
    private UUID figureId;

    public FigureBlock(UUID figureId) {
        this.figureId = figureId;
    }

    public UUID getFigureId() {
        return figureId;
    }

    public void setFigureId(UUID figureId) {
        this.figureId = figureId;
    }

    @Override
    public BlockType getType() {
        return BlockType.FIGURE;
    }
}
