assert !new File(basedir, 'node').exists() : "Node was installed in the custom install directory";
assert !new File(basedir, 'node/npm').exists() : "npm was copied to the node directory";

assert new File(basedir, 'node_modules').exists() : "Node modules were not installed in the base directory";

import org.codehaus.plexus.util.FileUtils;

String buildLog = FileUtils.fileRead(new File(basedir, 'build.log'));

assert buildLog.contains('BUILD SUCCESS') : 'build was not successful'
