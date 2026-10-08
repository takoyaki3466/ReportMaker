package org.takoyaki.reportmaker.service;

import org.takoyaki.reportmaker.model.ReportDocument;

public interface ProjectDocumentCodec {
    String encode(ReportDocument document);
    ReportDocument decode(String json);
}
