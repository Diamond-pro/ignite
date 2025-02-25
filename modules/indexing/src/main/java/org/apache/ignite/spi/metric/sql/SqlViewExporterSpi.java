/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.spi.metric.sql;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.query.QueryUtils;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.internal.processors.query.h2.SchemaManager;
import org.apache.ignite.internal.processors.query.h2.sys.view.SqlAbstractLocalSystemView;
import org.apache.ignite.internal.processors.query.h2.sys.view.SqlSystemViewColumnCondition;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.spi.IgniteSpiAdapter;
import org.apache.ignite.spi.IgniteSpiContext;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.metric.Metric;
import org.apache.ignite.spi.metric.MetricExporterSpi;
import org.apache.ignite.spi.metric.ReadOnlyMetricRegistry;
import org.h2.engine.Session;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.value.Value;
import org.jetbrains.annotations.Nullable;

/**
 * This SPI implementation exports metrics as SQL views.
 */
public class SqlViewExporterSpi extends IgniteSpiAdapter implements MetricExporterSpi {
    /** System view name. */
    public static final String SYS_VIEW_NAME = "METRICS";

    /** Metric filter. */
    private @Nullable Predicate<Metric> filter;

    /** Metric Registry. */
    private ReadOnlyMetricRegistry mreg;

    /** {@inheritDoc} */
    @Override protected void onContextInitialized0(IgniteSpiContext spiCtx) throws IgniteSpiException {
        GridKernalContext ctx = ((IgniteEx)ignite()).context();

        SchemaManager mgr = ((IgniteH2Indexing)ctx.query().getIndexing()).schemaManager();

        mgr.createSystemView(QueryUtils.SCHEMA_MONITORING, new MetricSetLocalSystemView(ctx));

        if (log.isDebugEnabled())
            log.debug(SYS_VIEW_NAME + " SQL view for metrics created.");
    }

    /** {@inheritDoc} */
    @Override public void spiStart(@Nullable String igniteInstanceName) throws IgniteSpiException {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public void spiStop() throws IgniteSpiException {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public void setMetricRegistry(ReadOnlyMetricRegistry mreg) {
        this.mreg = mreg;
    }

    /** {@inheritDoc} */
    @Override public void setExportFilter(Predicate<Metric> filter) {
        this.filter = filter;
    }

    /** */
    public class MetricSetLocalSystemView extends SqlAbstractLocalSystemView {
        /**
         * @param ctx Context.
         */
        public MetricSetLocalSystemView(GridKernalContext ctx) {
            super(SYS_VIEW_NAME, "Ignite metrics",
                ctx,
                newColumn("NAME", Value.STRING),
                newColumn("VALUE", Value.STRING),
                newColumn("DESCRIPTION", Value.STRING));
        }

        /** {@inheritDoc} */
        @Override public Iterator<Row> getRows(Session ses, SearchRow first, SearchRow last) {
            SqlSystemViewColumnCondition nameCond = conditionForColumn("NAME", first, last);

            Collection<Metric> metrics;

            if (nameCond.isEquality()) {
                Metric metric = mreg.findMetric(nameCond.valueForEquality().getString());

                metrics = metric == null ? Collections.emptySet() : Collections.singleton(metric);
            }
            else
                metrics = mreg.getMetrics();

            return F.iterator(metrics,
                m -> createRow(ses,
                    m.name(),
                    m.getAsString(),
                    m.description()),
                true, m -> filter.test(m));
        }
    }
}
