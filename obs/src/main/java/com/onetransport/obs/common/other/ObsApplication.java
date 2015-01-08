/**
 * ObsApplication.java
 *
 * Created by Gan Jianping on 07/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package com.onetransport.obs.common.other;

import org.ganjp.glib.core.BaseApplication;
import com.onetransport.obs.common.dao.SystemDAO;
import com.onetransport.obs.common.dao.ObsDaoFactory;
import com.onetransport.obs.common.dao.ObsDaoFactory.DAOType;


/**
 * <p>Base Application</p>
 * 
 * @author GanJianping
 * @since v1.0.0
 */
public class ObsApplication extends BaseApplication {
	
	/**
	 * <p>Get System DAO</p>
	 */
	public static SystemDAO getSystemDAO() {
		return (SystemDAO) (ObsDaoFactory.getInstance().getDAO(DAOType.SYSTEM, ObsApplication.getAppContext()));
	}
}	
