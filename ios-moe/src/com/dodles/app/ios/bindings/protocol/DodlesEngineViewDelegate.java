package com.dodles.app.ios.bindings.protocol;


import com.dodles.app.ios.bindings.DodlesEngineView;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCProtocolName;
import org.moe.natj.objc.ann.Selector;

@Generated
@Runtime(ObjCRuntime.class)
@ObjCProtocolName("DodlesEngineViewDelegate")
public interface DodlesEngineViewDelegate {
	@Generated
	@Selector("sendEngineEventToReact:topic:type:data:")
	void sendEngineEventToReactTopicTypeData(DodlesEngineView view,
			String topic, String type, String data);
}