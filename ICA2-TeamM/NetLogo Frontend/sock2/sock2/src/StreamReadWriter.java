/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author u0020379
 */
public interface StreamReadWriter {
	public void clear();
	public String peek();
	public String poll();
	public String take();
	public boolean isConnected();
	public boolean isEmpty();
	public boolean isInputWaiting();
}
