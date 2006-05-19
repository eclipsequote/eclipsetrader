/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.core.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Alert extends PersistentObject
{
    private String pluginId;
    private List targets = new ArrayList();
    private Map parameters = new HashMap();

    public Alert()
    {
    }

    public Alert(Integer id)
    {
        super(id);
    }

    public String getPluginId()
    {
        return pluginId;
    }

    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
        setChanged();
    }

    public List getTargets()
    {
        return targets;
    }

    public void setTargets(List targets)
    {
        this.targets = targets;
    }

    public Map getParameters()
    {
        return parameters;
    }

    public void setParameters(Map parameters)
    {
        this.parameters = parameters;
        setChanged();
    }
}
