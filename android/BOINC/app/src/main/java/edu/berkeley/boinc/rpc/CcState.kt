/*
 * This file is part of BOINC.
 * http://boinc.berkeley.edu
 * Copyright (C) 2020 University of California
 *
 * BOINC is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * BOINC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BOINC.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.berkeley.boinc.rpc

import java.util.ArrayList

data class CcState(
        var versionInfo: VersionInfo? = null,
        var hostInfo: HostInfo? = null,
        var haveAti: Boolean = false,
        var haveCuda: Boolean = false,
        val projects: MutableList<Project> = ArrayList(),
        val apps: MutableList<App> = ArrayList(),
        val appVersions: MutableList<AppVersion> = ArrayList(),
        val workunits: MutableList<Workunit> = ArrayList(),
        val results: MutableList<Result> = ArrayList()
) {
    fun clearArrays() {
        projects.clear()
        apps.clear()
        appVersions.clear()
        workunits.clear()
        results.clear()
    }

    fun lookupApp(project: Project?, appName: String?): App? {
        return apps.filter { it.project == project }
                .firstOrNull { it.name.equals(appName, ignoreCase = true) }
    }

    fun lookupWorkUnit(project: Project?, workUnitName: String?): Workunit? {
        return workunits.filter { it.project == project }
                .firstOrNull { it.name.equals(workUnitName, ignoreCase = true) }
    }

    fun lookupAppVersion(project: Project?, app: App?, versionNum: Int, planClass: String?): AppVersion? {
        // Sequences process elements lazily, which can improve performance with large collections and
        // complex operations.
        return appVersions.asSequence()
                .filter { it.project == project } //Check if projects match
                .filter { it.app == app } //Check if app matches
                .filter { it.version_num == versionNum } //Check version_num
                .firstOrNull { it.plan_class.equals(planClass, ignoreCase = true) } //Check plan class
    }

    object Fields {
        const val HAVE_ATI = "have_ati"
        const val HAVE_CUDA = "have_cuda"
    }
}
