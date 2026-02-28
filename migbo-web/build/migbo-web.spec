Summary: migBo Web Site
Name: migbo-web
Version: %{_version}
Release: %{_release}
License: proprietary
Group: Applications/Blogging
BuildArchitectures: noarch

Packager: Clive Cleland <clive@sprybts.com>

%define _build_path /var/www/migbo
%define _rpm_file_list %{_tmppath}/%{name}-%{version}-%{release}-file.list

Source: %{name}-%{version}.tar.gz
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-root

%description
migBo web site

%prep

%setup -c

%build

%install
%{__rm} -rf %{buildroot}
%{__install} -d -m0755 %{buildroot}%{_build_path}

cp -rfp * %{buildroot}

%{__rm} -rf %{buildroot}%{_build_path}/etc
%{__rm} -rf %{buildroot}%{_build_path}/build
%{__rm} -rf %{buildroot}%{_build_path}/.gitignore
find %{buildroot}%{_build_path}/application/config -depth -type d \! -name config -exec /bin/rm -rf {} \;

# Build the list of files
find %{buildroot}%{_build_path} \( -type f -o -type l \) -true -print | \
	sed -r -e 's:%{buildroot}::' \
		-e 's/^/\"/;s/$/\"/' \
		-e 's[("%{_build_path}/application/config/environment.php")[%config(noreplace) \1[' \
		-e 's[("%{_build_path}/application/config/flood_control.php")[%config(noreplace) \1[' \
		-e 's[("%{_build_path}/application/config/migcore/urls.php")[%config(noreplace) \1[' > %{_rpm_file_list}

%clean
%{__rm} -rf %{buildroot} %{_tmppath}/%{name}-%{version}.tar.gz
%{__rm} -f %{_rpm_file_list}

%files -f %{_rpm_file_list}

%defattr(-, root, root, 0755)

%changelog
* Tue Dec 3 2013 Andrew Kent <andrew@sprybts.com> 2.0-1
- Hydra-ci package builder from local source file

* Sat Jul 19 2013 Chern Jie <chern.jie@mig33global.com> 1.0-1
- Package from GitHub instead of Subversion

* Wed Sep 20 2011 Clive Cleland <clive@SpryBTS.com> 0.01-1
- Update for svn tagging 

* Wed Sep  6 2011 Clive Cleland <clive@SpryBTS.com> 0.0.2-1
- Update for svn changes

* Wed Aug 24 2011 Clive Cleland <clive@SpryBTS.com> 0.0.1-1
- Rewrite for Subversion Packaging
