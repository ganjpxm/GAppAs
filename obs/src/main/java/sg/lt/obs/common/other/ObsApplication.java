/**
 * ObsApplication.java
 *
 * Created by Gan Jianping on 07/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs.common.other;

import org.ganjp.glib.core.BaseApplication;
import sg.lt.obs.common.dao.SystemDAO;
import sg.lt.obs.common.dao.ObsDaoFactory;
import sg.lt.obs.common.dao.ObsDaoFactory.DAOType;


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
