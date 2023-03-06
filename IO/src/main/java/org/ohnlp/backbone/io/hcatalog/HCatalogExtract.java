package org.ohnlp.backbone.io.hcatalog;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.beam.sdk.io.hcatalog.HCatToRow;
import org.apache.beam.sdk.io.hcatalog.HCatalogBeamSchema;
import org.apache.beam.sdk.io.hcatalog.HCatalogIO;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionRowTuple;
import org.apache.beam.sdk.values.Row;
import org.ohnlp.backbone.api.Extract;
import org.ohnlp.backbone.api.annotations.ComponentDescription;
import org.ohnlp.backbone.api.annotations.ConfigurationProperty;
import org.ohnlp.backbone.api.exceptions.ComponentInitializationException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts items from HCatalog (e.g. Hive) stores. <br/>
 * Expected Configuration:
 * <code>
 *     {
 *         "metastore_uris": "thrift://metastore-host:port",
 *         "database": "source_database_name",
 *         "table": "source_table_name"
 *     }
 * </code>
 */
@ComponentDescription(
        name = "Read Records from HCatalog (e.g. Hive) stores",
        desc = "Reads Records from HCatalog stores. Expected input is a table (and/or view). Notably, queries are not supported." +
                "The output schema will correspond to the source table/view."
)
public class HCatalogExtract extends Extract {
    Map<String, String> configProperties;
    @ConfigurationProperty(
            path = "metastore_uris",
            desc = "The HCatalog metastore URIs in the format thrift://metastore-host:port"
    )
    private String metaStoreURIs;
    @ConfigurationProperty(
            path = "database",
            desc = "The HCatalog database to read from"
    )
    private String database;
    @ConfigurationProperty(
            path = "table",
            desc = "The HCatalog table to read from"
    )
    private String table;

    @Override
    public void init() throws ComponentInitializationException {
        this.configProperties = new HashMap<>();
        this.configProperties.put("hive.metastore.uris", metaStoreURIs);
    }

    @Override
    public List<String> getOutputTags() {
        return Collections.singletonList("HCatalog Records: " + table);
    }

    @Override
    public Map<String, Schema> calculateOutputSchema(Map<String, Schema> input) {
        HCatalogBeamSchema hcatSchema = HCatalogBeamSchema.create(configProperties);
        Schema schema = hcatSchema.getTableSchema(database, table).get();
        return Collections.singletonMap(getOutputTags().get(0), schema);
    }

    @Override
    public PCollectionRowTuple expand(PBegin input) {
        return PCollectionRowTuple.of(
                getOutputTags().get(0),
                HCatToRow.fromSpec(HCatalogIO.read().withConfigProperties(configProperties)
                .withDatabase(database)
                .withTable(table)).expand(input)
        ); // Row schema already set/embedded in transform
    }
}
