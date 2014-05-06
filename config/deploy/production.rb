set :stage, :production
set :ec2_contact_point, :private_ip
set :foreman_template, -> { release_path.join('config','upstart') }
set :foreman_sudo, true
set :upstart_sudo, true
set :use_sudo, true

ec2_role :commsinternal,
  user: 'deploy',
  ssh_options: {
    forward_agent: true,
    port: 8008
  }
