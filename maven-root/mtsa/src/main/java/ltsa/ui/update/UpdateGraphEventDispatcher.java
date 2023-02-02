package ltsa.ui.update;

/**
 * Created by Victor Wjugow on 18/06/15.
 */
public interface UpdateGraphEventDispatcher {

	void addUpdateGraphEventListener(UpdateGraphEventListener listener);

	void removeAllListeners();
}