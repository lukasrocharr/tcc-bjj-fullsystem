package com.academia.bjj.graduacao.service;

import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.graduacao.model.Graduacao;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Geracao do certificado de graduacao em PDF com OpenPDF (RF-073).
 */
@Service
public class CertificadoPdfService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color DOURADO = new Color(201, 168, 76);

    public byte[] gerar(Graduacao g) {
        Document doc = new Document(PageSize.A4.rotate(), 48, 48, 60, 60);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30, DOURADO);
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 16, Color.DARK_GRAY);
            Font nome = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, Color.BLACK);
            Font corpo = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.BLACK);

            doc.add(espaco(2));
            doc.add(centralizado("CERTIFICADO DE GRADUACAO", titulo));
            doc.add(centralizado("Academia de Jiu-Jitsu", subtitulo));
            doc.add(espaco(2));

            doc.add(centralizado("Certificamos que", corpo));
            doc.add(centralizado(g.getAluno().getNome(), nome));
            doc.add(espaco(1));

            String faixaTexto = "foi graduado(a) a faixa " + g.getFaixa().getNome()
                    + (g.getGraus() > 0 ? " - " + g.getGraus() + "o grau" : "");
            doc.add(centralizado(faixaTexto, corpo));
            doc.add(centralizado("em " + g.getData().format(DATA), corpo));

            if (g.getProfessor() != null) {
                doc.add(espaco(2));
                doc.add(centralizado("Professor responsavel: " + g.getProfessor().getNome(), corpo));
            }
            if (g.getObservacao() != null && !g.getObservacao().isBlank()) {
                doc.add(espaco(1));
                doc.add(centralizado(g.getObservacao(), subtitulo));
            }

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Falha ao gerar o certificado em PDF");
        }
    }

    private Paragraph centralizado(String texto, Font font) {
        Paragraph p = new Paragraph(texto, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(8);
        return p;
    }

    private Paragraph espaco(int linhas) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(linhas * 10f);
        return p;
    }
}
