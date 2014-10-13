package edu.ucsd.placeit.service;

import java.util.Date;

public interface IVersionHandler {
	public int handleConflicts();
	public Date getOnlineVersion();
	public Date getLocalVersion();
}
