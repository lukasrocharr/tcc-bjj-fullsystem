package com.academia.bjj.financeiro.service;

import com.academia.bjj.common.exception.BusinessException;
import com.academia.bjj.financeiro.model.Mensalidade;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Recibo de mensalidade paga em PDF (RF-080).
 */
@Service
public class ReciboPdfService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color DOURADO = new Color(201, 168, 76);

    public byte[] gerar(Mensalidade m) {
        if (m.getDataPagamento() == null) {
            throw new BusinessException("Mensalidade ainda nao foi paga");
        }
        Document doc = new Document(PageSize.A4, 56, 56, 60, 60);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, DOURADO);
            Font label = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
            Font valor = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            Paragraph t = new Paragraph("RECIBO DE PAGAMENTO", titulo);
            t.setAlignment(Element.ALIGN_CENTER);
            t.setSpacingAfter(6);
            doc.add(t);

            Paragraph sub = new Paragraph("Academia de Jiu-Jitsu", valor);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(24);
            doc.add(sub);

            PdfPTable tabela = new PdfPTable(2);
            tabela.setWidthPercentage(100);
            tabela.setWidths(new int[]{1, 2});

            linha(tabela, "Aluno", m.getMatricula().getAluno().getNome(), label, valor);
            linha(tabela, "Plano", m.getMatricula().getPlano().getNome(), label, valor);
            linha(tabela, "Competencia", String.format("%02d/%d", m.getMes(), m.getAno()), label, valor);
            linha(tabela, "Valor base", "R$ " + m.getValor(), label, valor);
            linha(tabela, "Multa", "R$ " + m.getMulta(), label, valor);
            linha(tabela, "Juros", "R$ " + m.getJuros(), label, valor);
            linha(tabela, "Total pago", "R$ " + m.getValorPago(), label, valor);
            linha(tabela, "Data do pagamento", m.getDataPagamento().format(DATA), label, valor);

            doc.add(tabela);

            Paragraph rodape = new Paragraph(
                    "\nRecibo gerado eletronicamente. Pagamento confirmado.", valor);
            rodape.setAlignment(Element.ALIGN_CENTER);
            rodape.setSpacingBefore(24);
            doc.add(rodape);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Falha ao gerar o recibo em PDF");
        }
    }

    private void linha(PdfPTable tabela, String rotulo, String valorTexto, Font fLabel, Font fValor) {
        PdfPCell c1 = new PdfPCell(new Paragraph(rotulo, fLabel));
        PdfPCell c2 = new PdfPCell(new Paragraph(valorTexto, fValor));
        c1.setPadding(8);
        c2.setPadding(8);
        tabela.addCell(c1);
        tabela.addCell(c2);
    }
}
