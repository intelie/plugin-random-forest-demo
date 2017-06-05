package net.intelie.live.demo.randomforest;

import net.intelie.live.HtmlTag;
import net.intelie.live.Live;
import net.intelie.live.LivePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.util.MulticoreExecutor;

public class Main implements LivePlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public void start(Live live) throws Exception {
        live.engine().addExtensionType(new RFExtensionType(live));
        live.web().addContent("ui.js", getClass().getResource("/ui.js"));
        live.web().addContent("icon.png", getClass().getResource("/icon.png"));
        live.web().addTag(HtmlTag.Position.BEGIN, new HtmlTag.JsFile(live.web().resolveContent("ui.js")));
        live.describeAction("Bad threadpool", MulticoreExecutor::shutdown);
    }


}
