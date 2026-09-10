/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.stockmanagement.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.openlmis.stockmanagement.domain.JasperTemplate;
import org.openlmis.stockmanagement.exception.JasperReportViewException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class JasperReportServiceIntegrationTest {

  private static final String EMPTY_REPORT_RESOURCE = "/empty-report.jrxml";
  private static final int DOUBLE_HIKARI_DEFAULT_POOL_SIZE = 20;
  private static final String DATE_FORMAT = "dd/MM/yyyy";
  private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
  private static final String FORMAT = "format";
  private static final String PDF = "pdf";
  private static final String ERROR_GENERATE_REPORT_FAILED =
      "stockmanagement.error.generateReport.failed";
  private static final String REASON = "Damage";
  private static final String REASON_FREE_TEXT = "broken in transit";
  private static final String SOURCE = "Central Warehouse";
  private static final String SOURCE_FREE_TEXT = "via courier";
  private static final String DESTINATION = "Kanengo Clinic";
  private static final String ORPHAN_FREE_TEXT = "orphan text";
  private static final String REASON_WITH_FREE_TEXT = "Damage: broken in transit";
  private static final String SOURCE_WITH_FREE_TEXT = "Central Warehouse: via courier";
  private static final String OLD_SEPARATOR = " : ";
  private static final String OLD_PARENTHESISED_FREE_TEXT = "(broken in transit)";

  private static final ListResourceBundle TRANSLATIONS = new ListResourceBundle() {
    @Override
    protected Object[][] getContents() {
      return new Object[][] {
          {"report.pattern.label", "{0}:"},
          {"report.pattern.labelledValue", "{0}: {1}"}
      };
    }
  };

  @InjectMocks
  private JasperReportService service;

  @Spy
  private DataSource dataSource;

  private ByteArrayOutputStream bos = new ByteArrayOutputStream();
  private ObjectOutputStream out;
  private JasperTemplate template = new JasperTemplate();
  private Map<String, Object> params = new HashMap<>();

  @Before
  public void setUp() throws IOException {
    out = new ObjectOutputStream(bos);
  }

  @Test
  public void generateReportShouldNotThrowErrorAfterPrintingReport20Times()
      throws JRException, IOException, JasperReportViewException {
    out.writeObject(getEmptyReport());
    out.flush();

    template.setData(bos.toByteArray());
    params.put(FORMAT, PDF);

    for (int i = 0; i <= DOUBLE_HIKARI_DEFAULT_POOL_SIZE; i++) {
      service.generateReport(template, params);
    }
  }

  @Test
  public void shouldGenerateReportForDatasourceParam()
      throws JRException, IOException {
    out.writeObject(getEmptyReport());
    out.flush();

    template.setData(bos.toByteArray());
    params.put("datasource", new ArrayList<>());

    service.generateReport(template, params);
  }

  @Test
  public void shouldGenerateReportWithProperParams()
      throws JRException, IOException {
    out.writeObject(getEmptyReport());
    out.flush();

    template.setData(bos.toByteArray());
    params.put("dateTimeFormat", DATE_TIME_FORMAT);
    params.put("dateFormat", DATE_FORMAT);
    params.put(FORMAT, PDF);

    service.generateReport(template, params);
  }

  @Test
  public void shouldCatchJasperReportViewExceptionWhenDatasourceReturnsNull()
      throws JRException, IOException, SQLException {
    out.writeObject(getEmptyReport());
    out.flush();

    template.setData(bos.toByteArray());
    params.put(FORMAT, PDF);

    when(dataSource.getConnection()).thenThrow(NullPointerException.class);
    try {
      service.generateReport(template, params);
    } catch (JasperReportViewException e) {
      assertTrue(e.getMessage().contains(ERROR_GENERATE_REPORT_FAILED));
    }
  }

  @Test
  public void shouldCatchJasperReportViewExceptionWhenDatasourceReturnsSqlException()
      throws JRException, IOException, SQLException {
    out.writeObject(getEmptyReport());
    out.flush();

    template.setData(bos.toByteArray());
    params.put(FORMAT, PDF);

    when(dataSource.getConnection()).thenThrow(SQLException.class);
    try {
      service.generateReport(template, params);
    } catch (JasperReportViewException e) {
      assertTrue(e.getMessage().contains(ERROR_GENERATE_REPORT_FAILED));
    }
  }

  @Test
  public void shouldCatchJasperReportViewExceptionWhenDatasourceConnectionIsNotClosed()
      throws JRException, IOException, SQLException {
    out.writeObject(getEmptyReport());
    out.flush();

    template.setData(bos.toByteArray());
    params.put(FORMAT, PDF);

    when(dataSource.getConnection())
        .thenAnswer(invocation -> {
          throw new IOException("Connection not closed");
        });

    try {
      service.generateReport(template, params);
    } catch (JasperReportViewException e) {
      assertTrue(e.getMessage().contains(ERROR_GENERATE_REPORT_FAILED));
    }
  }

  @Test
  public void shouldCatchJasperReportViewExceptionIfNullPointerExceptionIsCaught()
      throws JRException, IOException, SQLException {
    JasperReport compiledReport = getEmptyReport();
    out.writeObject(compiledReport);
    out.flush();

    template.setData(bos.toByteArray());
    params.put(FORMAT, PDF);

    when(JasperFillManager.fillReport(compiledReport, params, dataSource.getConnection()))
        .thenThrow(NullPointerException.class);
    try {
      service.generateReport(template, params);
    } catch (JasperReportViewException e) {
      assertTrue(e.getMessage().contains(ERROR_GENERATE_REPORT_FAILED));
    }
  }

  @Test
  public void shouldCatchJasperReportViewExceptionIfJreExceptionIsCaught()
      throws JRException, IOException, SQLException {
    JasperReport compiledReport = getEmptyReport();
    out.writeObject(compiledReport);
    out.flush();

    template.setData(bos.toByteArray());
    params.put(FORMAT, PDF);

    when(JasperFillManager.fillReport(compiledReport, params, dataSource.getConnection()))
        .thenAnswer(invocation -> {
          throw new JRException("Jasper Report Exception");
        });

    try {
      service.generateReport(template, params);
    } catch (JasperReportViewException e) {
      assertTrue(e.getMessage().contains(ERROR_GENERATE_REPORT_FAILED));
    }
  }

  @Test(expected = JasperReportViewException.class)
  public void shouldThrowJasperReportViewExceptionIfReportIsNotSavedAsObjectOutputStream() {
    template.setData(bos.toByteArray());
    params.put(FORMAT, PDF);

    service.generateReport(template, params);
  }

  @Test(expected = JasperReportViewException.class)
  public void shouldThrowJasperReportViewExceptionWhenNoParamsPassed()
      throws JRException, IOException {
    out.writeObject(getEmptyReport());
    out.flush();

    template.setData(bos.toByteArray());

    service.generateReport(template, null);
  }

  @Test
  public void shouldCompileEveryTemplateTheServiceGenerates() throws JRException {
    for (String templateUrl : Arrays.asList(JasperReportService.CARD_REPORT_URL,
        JasperReportService.CARD_SUMMARY_REPORT_URL, JasperReportService.STOCK_EVENT_REPORT_URL,
        JasperReportService.PI_LINES_REPORT_URL)) {
      assertNotNull(templateUrl, compile(templateUrl));
    }
  }

  @Test
  public void shouldPrintStockCardNamesWithFreeText() throws JRException {
    Map<String, Object> lineItemFields = new HashMap<>();
    lineItemFields.put("occurredDate", "2026-09-09");
    lineItemFields.put("quantity", 5);
    lineItemFields.put("stockOnHand", 100);
    lineItemFields.put("username", "srmanager");
    lineItemFields.put("signature", "sig");
    lineItemFields.put("documentNumber", "DOC-1");

    Map<String, Object> stockCard = new HashMap<>();
    stockCard.put("stockOnHand", 100);
    stockCard.put("facility", named("Balaka District Hospital"));
    stockCard.put("program", named("Family Planning"));
    stockCard.put("orderable", named("Levora"));
    stockCard.put("lot", named("LOT-1"));
    stockCard.put("lineItems", Arrays.asList(
        line(lineItemFields, "reason", named(REASON), "reasonFreeText", REASON_FREE_TEXT),
        line(lineItemFields, "source", named(SOURCE), "sourceFreeText", SOURCE_FREE_TEXT),
        line(lineItemFields, "destination", named(DESTINATION), "destinationFreeText", ""),
        line(lineItemFields, "reason", null, "reasonFreeText", ORPHAN_FREE_TEXT)));

    Map<String, Object> reportParams = freeTextReportParameters();
    reportParams.put("hasLot", Boolean.TRUE);
    reportParams.put("orderableNetContent", 10);

    List<Map<String, ?>> rows = new ArrayList<>();
    rows.add(stockCard);
    assertNamesPrintedWithFreeText(fill(JasperReportService.CARD_REPORT_URL, rows, reportParams));
  }

  @Test
  public void shouldPrintStockEventNamesWithFreeText() throws JRException {
    Map<String, Object> lineFields = new HashMap<>();
    lineFields.put("documentnumber", "DOC-1");
    lineFields.put("processeddate", new java.sql.Timestamp(0L));
    lineFields.put("signature", "sig");
    lineFields.put("eventorigin", "ADJUSTMENT");
    lineFields.put("facility", "Balaka District Hospital");
    lineFields.put("program", "Family Planning");
    lineFields.put("firstname", "Stock");
    lineFields.put("lastname", "Manager");
    lineFields.put("productcode", "CODE-1");
    lineFields.put("productname", "Levora");
    lineFields.put("netcontent", 10);
    lineFields.put("lotcode", "LOT-1");
    lineFields.put("occurreddate", "2026-09-09");
    lineFields.put("quantity", 5);
    lineFields.put("stockonhand", 100);

    Map<String, Object> reportParams = freeTextReportParameters();
    reportParams.put("stockEventId", "b7f4d1f0-0d5e-4c2c-9a7a-1d6a1b3f0a11");
    reportParams.put("dateTimeFormat", DATE_TIME_FORMAT);
    reportParams.put("timeZoneId", "UTC");

    List<Map<String, ?>> rows = Arrays.asList(
        line(lineFields, "reasonname", REASON, "reasonfreetext", REASON_FREE_TEXT),
        line(lineFields, "source", SOURCE, "sourcefreetext", SOURCE_FREE_TEXT),
        line(lineFields, "destination", DESTINATION, "destinationfreetext", ""),
        line(lineFields, "reasonname", null, "reasonfreetext", ORPHAN_FREE_TEXT));

    assertNamesPrintedWithFreeText(
        fill(JasperReportService.STOCK_EVENT_REPORT_URL, rows, reportParams));
  }

  private void assertNamesPrintedWithFreeText(List<String> cells) {
    String printed = cells.toString();
    assertTrue(printed, cells.contains(REASON_WITH_FREE_TEXT));
    assertTrue(printed, cells.contains(SOURCE_WITH_FREE_TEXT));
    assertTrue("empty free text must print the name alone: " + printed,
        cells.contains(DESTINATION));
    assertTrue("a missing name must print the free text alone: " + printed,
        cells.contains(ORPHAN_FREE_TEXT));
    for (String cell : cells) {
      assertFalse("stray separator in " + cell, cell.contains(OLD_SEPARATOR));
      assertFalse("parenthesised free text in " + cell,
          cell.contains(OLD_PARENTHESISED_FREE_TEXT));
    }
  }

  private Map<String, Object> freeTextReportParameters() throws JRException {
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("dateFormat", DATE_FORMAT);
    reportParams.put("decimalFormat", new DecimalFormat("#,###"));
    reportParams.put("showInDoses", Boolean.TRUE);
    reportParams.put("headerTemplate", getEmptyReport());
    reportParams.put("REPORT_RESOURCE_BUNDLE", TRANSLATIONS);
    return reportParams;
  }

  private Map<String, Object> named(String name) {
    Map<String, Object> value = new HashMap<>();
    value.put("name", name);
    value.put("code", "CODE-1");
    value.put("netContent", 10);
    value.put("lotCode", "LOT-1");
    return value;
  }

  private Map<String, Object> line(Map<String, Object> commonFields, String namedField,
      Object namedValue, String freeTextField, String freeText) {
    Map<String, Object> line = new HashMap<>(commonFields);
    line.put(namedField, namedValue);
    line.put(freeTextField, freeText);
    return line;
  }

  /**
   * Fills a template from an in-memory data source, which makes JasperReports skip the template
   * query, and returns the text of every printed cell.
   */
  private List<String> fill(String templateUrl, List<Map<String, ?>> rows,
      Map<String, Object> reportParams) throws JRException {
    JasperPrint print = JasperFillManager.fillReport(compile(templateUrl), reportParams,
        new JRMapCollectionDataSource(rows));

    List<String> cells = new ArrayList<>();
    Deque<JRPrintElement> pending = new ArrayDeque<>();
    print.getPages().forEach(page -> pending.addAll(page.getElements()));
    while (!pending.isEmpty()) {
      JRPrintElement element = pending.removeFirst();
      if (element instanceof JRPrintFrame) {
        pending.addAll(((JRPrintFrame) element).getElements());
      } else if (element instanceof JRPrintText) {
        String text = ((JRPrintText) element).getFullText();
        if (text != null && !text.trim().isEmpty()) {
          cells.add(text);
        }
      }
    }
    return cells;
  }

  private JasperReport compile(String templateUrl) throws JRException {
    return JasperCompileManager.compileReport(getClass().getResourceAsStream(templateUrl));
  }

  private JasperReport getEmptyReport() throws JRException {
    return JasperCompileManager
        .compileReport(getClass().getResourceAsStream(EMPTY_REPORT_RESOURCE));
  }
}
