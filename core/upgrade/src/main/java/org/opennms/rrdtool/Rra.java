/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.rrdtool;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class RRA (Round Robin Archives).
 * <p>Warning: This representation doesn't support Aberrant Behavior Detection with Holt-Winters Forecasting</p>
 * 
 * @author Alejandro Galue <agalue@opennms.org>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Rra {

    /** The consolidation function. */
    private CFType consolidationFunction;

    /** The PDP (Primary Data Points) per row. */
    private Integer pdpPerRow;

    /** The rows. */
    private List<Row> rows = new ArrayList<Row>();

    /** The parameters. */
    private Parameters parameters = new Parameters();

    /** The CDP Data. */
    private List<RraDS> dataSources = new ArrayList<RraDS>();

    /**
     * Gets the consolidation function.
     *
     * @return the consolidation function
     */
    @XmlElement(name="cf")
    public CFType getConsolidationFunction() {
        return consolidationFunction;
    }

    /**
     * Sets the consolidation function.
     *
     * @param consolidationFunction the new consolidation function
     */
    public void setConsolidationFunction(CFType consolidationFunction) {
        this.consolidationFunction = consolidationFunction;
    }

    /**
     * Gets the PDP (Primary Data Points) per row.
     *
     * @return the PDP (Primary Data Points) per row
     */
    @XmlElement(name="pdp_per_row")
    public Integer getPdpPerRow() {
        return pdpPerRow;
    }

    /**
     * Sets the PDP (Primary Data Points) per row.
     *
     * @param pdpPerRow the new PDP (Primary Data Points) per row
     */
    public void setPdpPerRow(Integer pdpPerRow) {
        this.pdpPerRow = pdpPerRow;
    }

    /**
     * Gets the rows.
     *
     * @return the rows
     */
    @XmlElement(name="row")
    @XmlElementWrapper(name="database")
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Sets the rows.
     *
     * @param rows the new rows
     */
    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    @XmlElement(name="params")
    public Parameters getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters.
     *
     * @param parameters the new parameters
     */
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the data sources.
     *
     * @return the data sources
     */
    @XmlElement(name="ds")
    @XmlElementWrapper(name="cdp_prep")
    public List<RraDS> getDataSources() {
        return dataSources;
    }

    /**
     * Sets the data sources.
     *
     * @param dataSources the new data sources
     */
    public void setDataSources(List<RraDS> dataSources) {
        this.dataSources = dataSources;
    }

    /**
     * Format equals.
     * TODO: Check the parameters and the RRA data sources
     * 
     * @param rra the RRA object
     * @return true, if successful
     */
    public boolean formatEquals(Rra rra) {
        if (this.consolidationFunction != null) {
            if (rra.consolidationFunction == null) return false;
            else if (!(this.consolidationFunction.equals(rra.consolidationFunction))) 
                return false;
        }
        else if (rra.consolidationFunction != null)
            return false;

        if (this.pdpPerRow != null) {
            if (rra.pdpPerRow == null) return false;
            else if (!(this.pdpPerRow.equals(rra.pdpPerRow))) 
                return false;
        }
        else if (rra.pdpPerRow != null)
            return false;

        if (this.rows != null) {
            if (rra.rows == null) return false;
            else if (!(this.rows.size() == rra.rows.size())) 
                return false;
        }
        else if (rra.rows != null)
            return false;

        return true;
    }

}