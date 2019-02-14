package me.m_3.tiqoL.coreloader.interfaces;

import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.coreloader.Core;

public interface ClassPointer {

	public default Core getCore(WSServer server) {
		return null;
	}
	
}
