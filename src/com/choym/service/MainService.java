package com.choym.service;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.choym.model.RepositoryLocationInfo;
import com.choym.util.SVNUtil;

@Service("MainService")
public class MainService {
	public String onSayHello() {
		return new String("Hello! ");
	}

	/**
	 * display all direcories with given path recursively
	 * @param rli
	 * @return
	 * @throws SVNException
	 */
	@SuppressWarnings("deprecation")
	public String onDisplayRepositoryTree(RepositoryLocationInfo rli) throws SVNException {
		StringBuilder sb = new StringBuilder();

		SVNUtil.setupLibrary();
		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(rli.getUrl()));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(rli.getUsername(),
				rli.getPassword().toCharArray());
		repository.setAuthenticationManager(authManager);

		sb.append("Repository Root: " + repository.getRepositoryRoot(true) + "\n");
		sb.append("Repository UUID: " + repository.getRepositoryUUID(true) + "\n\n");

		// check whether the url we are going to use corresponds to a directory
		SVNNodeKind nodeKind = repository.checkPath("", -1);
		if (nodeKind == SVNNodeKind.NONE) {
			throw new NullPointerException("There is no entry at '" + rli.getUrl() + "'.");
		} else if (nodeKind == SVNNodeKind.FILE) {
			throw new NullPointerException(
					"The entry at '" + rli.getUrl() + "' is a file while a directory was expected.");
		}

		// traverse the repository tree recursively
		listEntries(repository, "", sb);

		sb.append("Repository latest revision: " + repository.getLatestRevision());

		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	private void listEntries(SVNRepository repository, String path, StringBuilder sb) throws SVNException {
		Collection entries = repository.getDir(path, -1, null, (Collection) null);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			sb.append("/" + (path.equals("") ? "" : path + "/") + entry.getName() + " (author: " + entry.getAuthor()
					+ "; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")\n");
			if (entry.getKind() == SVNNodeKind.DIR) {
				listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName(), sb);
			}
		}
	}

	/**
	 * do check out (recursive)
	 * @param rli
	 * @return
	 * @throws SVNException
	 */
	@SuppressWarnings("deprecation")
	public Long onCheckout(RepositoryLocationInfo rli) throws SVNException {
		SVNUtil.setupLibrary();

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(rli.getUsername(),
				rli.getPassword().toCharArray());
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance(options, authManager);
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);

		File file = new File("D:\\" + rli.getUrl().substring(rli.getUrl().lastIndexOf("/")) + "\\");
		return updateClient.doCheckout(SVNURL.parseURIDecoded(rli.getUrl()), file, SVNRevision.HEAD, SVNRevision.HEAD,
				SVNDepth.INFINITY, true);
	}

	/**
	 * do update (recursive)
	 * @param rli
	 * @param file
	 * @return
	 * @throws SVNException
	 */
	public long onUpdate(RepositoryLocationInfo rli, File file) throws SVNException {
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(rli.getUsername(),
				rli.getPassword().toCharArray());
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance(options, authManager);
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		return updateClient.doUpdate(file, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
	}

	/**
	 * add file or directory to SVN repository
	 * @param rli
	 * @param files
	 * @throws SVNException
	 */
	public String onAdd(RepositoryLocationInfo rli, File[] files) throws SVNException {
		SVNClientManager clientManager = SVNClientManager.newInstance();
		for (File file : files) {
			clientManager.getWCClient().doAdd(file, true, file.isDirectory() ? true : false, true, SVNDepth.INFINITY,
					true, true, true);
		}
		
		return "OK";
	}
	
	/**
	 * delete file or directory from SVN repository
	 * @param rli
	 * @param files
	 * @return 
	 * @throws SVNException
	 */
	public String onDelete(RepositoryLocationInfo rli, File[] files) throws SVNException {
		SVNClientManager clientManager = SVNClientManager.newInstance();
		for (File file : files) {
			clientManager.getWCClient().doDelete(file, true, true, false);
		}
		
		return "OK";
	}
	
	/**
	 * commit file/direcory to SVN repository (recursive)
	 * @param rli
	 * @param files
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public Object onCommit(RepositoryLocationInfo rli, File[] files, String commitMessage) throws SVNException {
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(rli.getUsername(),
				rli.getPassword().toCharArray());
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance(options, authManager);

		// then commit
		SVNCommitInfo info = clientManager.getCommitClient().doCommit(files, false, commitMessage, null, null, false,
				false, SVNDepth.INFINITY);
		if (info.getErrorMessage() != null) {
			throw new SVNException(info.getErrorMessage());
		}

		return info;
	}

	public String onShowStatus(RepositoryLocationInfo rli, File file) throws SVNException {
		SVNClientManager manager = SVNClientManager.newInstance();
		SVNStatusClient client = manager.getStatusClient();

		StringBuilder sb = new StringBuilder(file.getAbsolutePath() + ":\n----------\n");
		
		ISVNStatusHandler handler = new ISVNStatusHandler() {
			public void handleStatus(SVNStatus status) throws SVNException {
				sb.append("Content Status: " + status.getContentsStatus().toString() + "\n");
				sb.append("Is Versioned: " + status.isVersioned() + "\n");
				sb.append("Committed Version: " + status.getCommittedRevision().toString() + "\n");
				sb.append("Node Status: " + status.getNodeStatus().toString() + "\n");
			}
		};
		client.doStatus(new File("D:\\SQLInceptor\\file3.txt"), SVNRevision.UNDEFINED, SVNDepth.EMPTY, false, true,
				true, false, handler, null);
		
		return sb.toString();
	}
}
