package org.takoyaki.reportmaker.model;

import java.util.UUID;

public final class EquationBlock implements ReportBlock {
    private UUID equationId;

    public EquationBlock(UUID equationId) {
        this.equationId = equationId;
    }

    public UUID getEquationId() {
        return equationId;
    }

    public void setEquationId(UUID equationId) {
        this.equationId = equationId;
    }

    @Override
    public BlockType getType() {
        return BlockType.EQUATION;
    }
}
