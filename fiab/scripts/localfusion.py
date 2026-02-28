#! /usr/bin/python

import socket
import shutil
import re 
import time
import glob
import os
import sys
import optparse
import pprint
import termcolor

try:
    import Ice
    Ice.loadSlice("src/Fusion.ice")
    from com.projectgoth.fusion.slice import *
    ic = Ice.initialize()
    ice_initialized = True
except:
    ice_initialized = False

misc_apps = ['AuthenticationService','ReputationService','EventStore','EventSystem',
             'MessageLogger','SessionCache','BotService','BlueLabelService',
             'CricketFeed','UserNotificationService','JobSchedulingService',
             'RewardDispatcher','SMSEngine', 'VoiceEngine', 'ImageServer']

misc_apps_minimal = ['AuthenticationService','ReputationService','EventStore','EventSystem',
             'MessageLogger','SessionCache','BotService',
             'UserNotificationService',
             'RewardDispatcher']
def format_stats(prx, color=False):
    rows, columns = os.popen('stty size', 'r').read().split()
    label_width = 35
    value_width = 17
    optimal_columns = int(columns) / (label_width + value_width)
    label_width -= 2
    value_width -= 2
    stats = prx.getStats()
    def paint(s, clr):
        if color:
            return termcolor.colored(s, clr)
        else:
            return s
    
    result_string = "%s\n" % (paint(prx.ice_toString(), "red"))
    idx = 1 
    for key in stats.__dict__:
        val = getattr(stats, key)
        if hasattr( val, "__iter__"):
            pass
        else:
            result_string += "%s:%s\t" % (paint(key.ljust(label_width),"blue"), paint(str(val).ljust(value_width), "green"))
        
        idx += 1
        if idx == optimal_columns:
            idx = 0
            result_string += "\n"
    idx = 1
    result_string += "\n"
    for key in stats.__dict__:
        val = getattr(stats, key)
        if hasattr( val, "__iter__"):
            for el in val:
                if not isinstance(el, (int, str, list, dict)):
                    subidx = 1
                    result_string += "%s\n\t" % paint(key.ljust(label_width),"cyan")
                    for subkey in el.__dict__:
                        subidx += 1
                        if subidx == optimal_columns - 1:
                            subidx = 0
                            result_string += "\n\t"
                        result_string += "%s:%s\t" % (paint(subkey.ljust(label_width),"blue")
                                                    , paint(str(el.__dict__[subkey]).ljust(value_width), "green"))
                    result_string +="\n"
                else:
                    result_string += "%s\n\t%s" % (key.ljust(label_width), str(el).ljust(value_width))
    return result_string


def exec_cmd(cmd, opts):
    os.system(cmd)
    return 

def do_compile_and_stage(opts):
    output = exec_cmd("ant %s" % opts.compile_opts, opts)
    shutil.copyfile("target/artifacts/lib/fusion.jar", os.path.join(opts.fusion_base,"jboss","server/default/deploy/Fusion.jar"))
    shutil.copyfile("target/artifacts/lib/fusion.jar", os.path.join(opts.fusion_base,"fusion","Fusion.jar"))

def check_or_create_config(template_name, app_name, reps, opts):
    if not os.path.isfile ( os.path.join(opts.fusion_base,"fusion", "%s.cfg" % app_name ) ):
        print "auto generating config file for ObjectCache%d" % i
        template = open(os.path.join( opts.fusion_base,"fusion", "%s.cfg" % template_name)).read()
        out_file = open(os.path.join( opts.fusion_base,"fusion", "%s.cfg" % app_name ), "w")
        for rep in reps:
            template = template.replace(rep, str(reps[rep]))
        out_file.write(template)

def spin_generic(app_name, opts):
    exec_cmd(os.path.join(opts.fusion_base,"init.d/mig33.generic") + " restart %s" % app_name , opts)


def do_monitor_all(opts):
    def __load_proxies(): 
        # find all sockets bound to mig33 java apps
        pids = [k for k in os.popen("ps aux | grep java | awk '{print $2}'").read().split("\n") if k.strip()]
        ports = []
        for pid in pids:
            _ports = [k for k in os.popen("lsof -a -p%s -Pnl| grep LISTEN | awk '{print $9};' | cut -d ':' -f 2" % pid).read().split("\n") if k.strip()]
            ports.extend(_ports)
        ports = list(set(ports))
        new_ports = []
        for idx in range(0, len(ports)):
            try:
                conn = socket.create_connection(("localhost",int(ports[idx])))
                conn.settimeout(0.005)
                data = conn.recv(5)
                if data.find("Ice")>=0:
                    new_ports.append(ports[idx])
            except:
                pass
        ports = new_ports
        admin_proxies = [k for k in globals() if k.find("AdminPrx")>=0]
        # filter by user input
        if opts.monitor_apps:
            admin_proxies_ = []
            for prx in admin_proxies:
                for user_defined in opts.monitor_apps:
                    if prx.find(user_defined) >= 0:
                        admin_proxies_.append(prx)
                        break
            admin_proxies = admin_proxies_
        ice_adapters={}
        for idx in range(0, len(ports)):
            for proxy in admin_proxies:
                # try binding the port to a proxy
                try:
                    prxobj = globals()[proxy]
                    base = ic.stringToProxy("%s:tcp -p %s" % (proxy.replace("Prx",""), ports[idx]))
                    try:
                        prx = prxobj.checkedCast(base)
                        ice_adapters[prx]=prx
                        ports.remove(ports[idx])
                        break
                    except Exception,e:
                        pass
                except Exception,e:
                    pass
        return ice_adapters
    lastloaded = time.time()
    adapters = __load_proxies()
    while True:
        try:
            s = ""
            for adapter in sorted(adapters.keys()):
                try:
                    s+= format_stats(adapters[adapter], True) + "\n"
                except Exception, e:
                    adapters.pop(adapter)
            os.system("clear")
            print s
            time.sleep(1)
            now = time.time()
            if now - lastloaded > 10:
                adapters = __load_proxies()
                lastloaded = time.time()

        except KeyboardInterrupt,e:
            break
        
def main():
    parser = optparse.OptionParser()
    parser.add_option("-v",help="verbosity", dest="verbose", default=False, action="store_true")
    parser.add_option("","--start-only",help="starts the fusion apps without a deploy", dest="start_only", default=False, action="store_true")
    parser.add_option("-k",help="kill all fusion apps", dest="kill", default=False, action="store_true")
    parser.add_option("-m",help="monitor all fusion apps", dest="monitor", default=False, action="store_true")
    parser.add_option("-o",help="number of objectcaches", dest="num_objectcaches", default=1, type="int")
    parser.add_option("-g",help="number of gateways", dest="num_gateway",default=1, type="int")
    parser.add_option("-r",help="number of registries", dest="num_registries", default=1, type="int")
    parser.add_option("-e",help="number of eventqueue workers", dest="num_eventqueue", default=1, type="int")
    parser.add_option("","--minimal",help="run only minimal fusion apps", dest="minimal", action="store_true", default = False)
    parser.add_option("","--fusion-base",help="base path of fusion in a box", dest="fusion_base")
    parser.add_option("","--compile_opts",help="parameters to pass to ant", dest="compile_opts", default="clean-dist")
    parser.add_option("","--monitor-apps",help="apps to monitor", dest="monitor_apps", default="")

    opts, args = parser.parse_args()
    if not opts.fusion_base and os.environ.has_key("FUSION_BASE_PATH"):
        opts.fusion_base = os.path.abspath(os.path.expanduser(os.environ["FUSION_BASE_PATH"])) 
    else:
        opts.fusion_base = os.path.abspath(os.path.expanduser(opts.fusion_base)) 
    opts.monitor_apps = opts.monitor_apps.split(",")

    if opts.kill:
        exec_cmd("sudo " + os.path.join(opts.fusion_base,"init.d/jboss") + " stop", opts)
        for app in glob.glob(os.path.join(opts.fusion_base,"lock/*")):
            exec_cmd(os.path.join(opts.fusion_base,"init.d/mig33.generic") + " stop %s" % os.path.split(app)[1], opts)  
    elif opts.monitor:
        if ice_initialized:
            do_monitor_all(opts)
        else:
            print ("Ice-python not available, can't monitor")
    else:
        if not opts.start_only:
            do_compile_and_stage(opts)
        exec_cmd("sudo " + os.path.join(opts.fusion_base,"init.d/jboss") + " stop", opts)
        time.sleep(5)
        exec_cmd("sudo " + os.path.join(opts.fusion_base,"init.d/jboss") + " start", opts)
        time.sleep(5)
        
        for idx in range(1,opts.num_registries+1):
            check_or_create_config("Registry%d" % idx
                                    , "RegistryTemplate"
                                    , {"${adapterport}": 10000 + idx, "{nodeadapterport}": 8000+idx, "${adminport}": 12000+idx}
                                    , opts)
            spin_generic("Registry%d" % idx, opts)

        for idx in range(1,opts.num_objectcaches+1):
            check_or_create_config("ObjectCache%d" % idx
                    , "ObjectCacheTemplate"
                    , {"${adapterport}": 11000 + idx, "${adminport}": 9000+idx}
                    , opts)
            spin_generic("ObjectCache%d" % idx, opts)
        
        for idx in range(1,opts.num_gateway+1):
            check_or_create_config("GatewayTCP_%d" % (9119+idx)
                    , "GatewayTCP_Template"
                    , {"${adapterport}": 29000 + idx, "${adminport}": 39000+idx, "${gatewayport}":9119+idx}
                    , opts)
            spin_generic("GatewayTCP_%d" % (9119+idx), opts)
        
        for idx in range(1,opts.num_eventqueue+1):
            check_or_create_config("EventQueueWorker%d" % idx
                    , "EventQueueWorkerTemplate"
                    , {"${adapterport}": 17000 + idx, "${adminport}": 18000+idx}
                    , opts)
            spin_generic("EventQueueWorker%d" % idx, opts)
        if opts.minimal:
            for app in misc_apps_minimal:
                spin_generic(app, opts)
        else:
            for app in misc_apps:
                spin_generic(app, opts)





if __name__ == "__main__":
    main()
