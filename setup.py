import sys

from jcc import cpp

options = {
    'include': ('lib/org.eclipse.osgi.jar', '1.1/tika-app-1.1.jar',
        'lib/log4j.properties.jar'),
    'jar': ('1.1/tika-parsers-1.1.jar', '1.1/tika-core-1.1.jar',),
    'package': ('org.xml.sax',),
    'python': 'tika',
    'version': '1.1',
    'module': 'parser',
    'reserved': ('asm',),
    'classes': ('java.io.File', 'java.io.FileInputStream', 'java.io.ByteArrayInputStream'),
}

import sys
import os.path

jcc_args = []

for dir in sys.path:
    probe_dir = os.path.join(dir, 'jcc')
    if os.path.exists(probe_dir):
        print "Found jcc library at: %s" % probe_dir
        jcc_args = [os.path.join(probe_dir, 'nonexistent-argv-0')]
        break

if not jcc_args:
    raise Exception("tika library not found in sys.path: %s" % sys.path)

for k, v in options.iteritems():
    if k == 'classes':
        jcc_args.extend(v)
    elif hasattr(v, '__iter__'):
        for value in v:
            jcc_args.append('--%s' % k)
            jcc_args.append(value)
    else:
        jcc_args.append('--%s' % k)
        jcc_args.append(v)

setup_args = []
egg_info_mode = False
maxheap = '64m'

i = 1

while i < len(sys.argv):
    arg = sys.argv[i]

    if arg == 'install':
        jcc_args.append('--install')
    elif arg == 'build':
        jcc_args.append('--build')
    elif arg == '-c':
        # forwarded_args.append(arg)
        pass
    elif arg == 'egg_info':
        jcc_args.append('--egg-info')
    elif arg == '--vmarg':
        jcc_args.append(arg)
        i += 1
        jcc_args.append(sys.argv[i])
    elif arg == '--maxheap':
        i += 1
        maxheap = sys.argv[i]
    else:
        setup_args.append(arg)
    """
    elif arg == '--single-version-externally-managed':
        forwarded_args.append(arg)
    elif arg == '--egg-base' or arg == '--record':
        forwarded_args.append(arg)
        forward_next_arg = True
    else:
        raise NotImplementedError("Unknown argument: %s" % arg)
    """

    i += 1
    
for extra_arg in setup_args:
    jcc_args.append('--extra-setup-arg')
    jcc_args.append(extra_arg)

jcc_args.extend(['--maxheap', maxheap])

# monkey patch to send extra args to distutils
# import setuptools
# old_setup = setuptools.setup
"""
from distutils.core import setup as old_setup
def new_setup(**attrs):
	attrs['script_args'].extend(forwarded_args)
	print "running setup %s" % attrs['script_args']
	old_setup(**attrs)

def new_compile(**kwargs):
    print "changing jccPath from %s to %s" % (kwargs['jccPath'],
        jcc.__path__)
    kwargs['jccPath'] = jcc.__path__
    kwargs['setup_func'] = new_setup
    from jcc.python import compile
    compile(**kwargs, new_setup)
"""

print "jcc_args = %s" % jcc_args

cpp.jcc(jcc_args)
