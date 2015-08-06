load 'deploy'

set :application, "echo_proxy"
set :repository,  "git@github.com:sidoh/echo_proxy.git"
set :deploy_to, '/apps/echo_proxy'
set :tmp_dir, '/tmp/echo_proxy_tmp'

namespace :deploy do
  task :default do
    set :keep_releases, 10
    local_build
    copy_distribution
    restart
    create_symlink
    cleanup
  end
 
  task :local_build do
    run_locally "rm -rf #{tmp_dir} && mkdir -p #{tmp_dir}"
    run_locally "cp -r src bin lib pom.xml #{tmp_dir}"
    run_locally "cd #{tmp_dir} && mvn clean package"
    run_locally "cd #{tmp_dir} && tar czf echo_proxy-RELEASE.tar.gz src bin lib pom.xml target/echo_proxy*.jar"
  end

  task :copy_distribution do
    run "mkdir -p #{release_path}"

    run "mkdir -p #{shared_path}/log"
    run "mkdir -p #{shared_path}/tmp"

    run "ln -s #{shared_path}/* #{release_path}"

    top.upload "#{tmp_dir}/echo_proxy-RELEASE.tar.gz", "#{release_path}/echo_proxy-RELEASE.tar.gz"
    run "cd #{release_path} && tar xzf echo_proxy-RELEASE.tar.gz && rm -rf echo_proxy-RELEASE.tar.gz"
  end

  task :restart do
    run "cd #{release_path} && bin/stop && bin/start"
  end
  
  task :create_symlink do
    run "rm -rf #{current_path} && ln -s #{release_path} #{current_path}"
  end

  task :cleanup do
    run "ls -1dt /apps/echo_proxy/releases/* | tail -n +#{keep_releases} | xargs rm -rf"
  end
end
