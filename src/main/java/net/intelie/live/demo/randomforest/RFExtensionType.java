package net.intelie.live.demo.randomforest;

import net.intelie.live.*;

import java.util.Set;

public class RFExtensionType implements ExtensionType<RFConfig> {
    private final Live live;

    public RFExtensionType(Live live) {
        this.live = live;
    }

    @Override
    public String typename() {
        return "randomforest-demo";
    }

    @Override
    public ExtensionArea area() {
        return ExtensionArea.PLATFORM;
    }

    @Override
    public Set<ExtensionRole> roles() {
        return ExtensionRole.start().ok();
    }

    @Override
    public ElementHandle register(ExtensionQualifier qualifier, RFConfig config) throws Exception {
        return SafeElement.create(live, qualifier, config::create);
    }

    @Override
    public ElementState test(ExtensionQualifier qualifier, RFConfig config) throws Exception {
        SafeElement.create(live, qualifier, config::test).close();
        return ElementState.OK;
    }

    @Override
    public RFConfig parseConfig(String config) throws Exception {
        return LiveJson.fromJson(config, RFConfig.class);
    }
}
