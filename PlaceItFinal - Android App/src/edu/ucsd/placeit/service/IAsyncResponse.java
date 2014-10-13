package edu.ucsd.placeit.service;

import java.util.List;

public interface IAsyncResponse {
	List<String> processFinish(String output);
}
