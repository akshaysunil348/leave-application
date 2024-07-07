package org.joget.sample;

import java.util.ArrayList;
import java.util.Collection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();

        //Register plugin here
        registrationList.add(context.registerService(SamplePluginTool.class.getName(), new SamplePluginTool(), null));
        registrationList.add(context.registerService(StoreBinder.class.getName(), new StoreBinder(), null));
        registrationList.add(context.registerService(SampleWebRequest.class.getName(), new SampleWebRequest(), null));
        registrationList.add(context.registerService(JDBCBinder.class.getName(), new JDBCBinder(), null));
        registrationList.add(context.registerService(LeaveApplicationWebPlugin.class.getName(), new LeaveApplicationWebPlugin(), null));
        registrationList.add(context.registerService(LeaveRequestAcceptedPlugin.class.getName(), new LeaveRequestAcceptedPlugin(), null));
        registrationList.add(context.registerService(DataListBinder.class.getName(), new DataListBinder(), null));
        registrationList.add(context.registerService(ConditionDataListBinder.class.getName(), new ConditionDataListBinder(), null));


    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}