lock '3.1.0'

set :application, 'hypermq'
set :repo_url, 'git@github.com:uswitch/hypermq.git'
set :deploy_to, "/var/www/#{fetch(:application)}"

set :keep_releases, 3
set :user, 'deploy'

set :linked_dirs, %w{log .bundle vendor/bundle}

set :branch, ENV['branch'] || "master"
