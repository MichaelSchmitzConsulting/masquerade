package masquerade.sim.app.ui2.view.impl;

import java.util.List;

import masquerade.sim.app.ui2.view.ChannelView;
import masquerade.sim.model.listener.DeleteListener;
import masquerade.sim.model.ui.MasterDetailView;
import masquerade.sim.model.ui.MasterDetailView.AddListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.VerticalLayout;

/**
 * Tab content showing available channels
 */
@SuppressWarnings("serial")
public class ChannelViewImpl extends VerticalLayout implements ChannelView {
	private final MasterDetailView masterDetailView;
	private ChannelViewCallback callback;

	public ChannelViewImpl(FormFieldFactory fieldFactory) {
		setCaption("Listener");
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		ValueChangeListener selectionListener = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				callback.onSelection((ChannelInfo) event.getProperty().getValue());
			}
		};
		masterDetailView = new MasterDetailView(fieldFactory, false, selectionListener);
		addComponent(masterDetailView);
		setExpandRatio(masterDetailView, 1.0f);
		masterDetailView.addAddListener(new AddListener() {
			@Override public void onAdd() {
				callback.onAdd();
			}
		});
		masterDetailView.addDeleteListener(new DeleteListener() {
			@Override public void notifyDelete(Object obj) {
				ChannelInfo selection = (ChannelInfo) masterDetailView.getSelection();
				callback.onRemove(selection);
			}
		});
	}
	
	public void bind(ChannelViewCallback callback) {
		this.callback = callback;
		callback.onRefresh();
	}
	
	@Override
	public void setChannelList(List<ChannelInfo> channels) {
		BeanItemContainer<ChannelInfo> container = new BeanItemContainer<ChannelInfo>(ChannelInfo.class, channels);
		masterDetailView.setDataSource(container);
		masterDetailView.setDetailEditorBean(null);
	}

	@Override
	public void setDetailEditorBean(Object bean) {
		masterDetailView.setDetailEditorBean(bean);
	}
}
