package masquerade.sim.app.ui.dialog;

import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededListener;

public interface UploadHandler extends Receiver, SucceededListener, FailedListener, ProgressListener, StartedListener {

}
