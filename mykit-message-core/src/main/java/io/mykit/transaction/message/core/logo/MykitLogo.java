/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.transaction.message.core.logo;

import io.mykit.transaction.message.common.constant.CommonConstant;
import io.mykit.transaction.message.common.utils.VersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author binghe
 * @version 1.0.0
 * @description MykitLogo
 */
public class MykitLogo {

    private static final String MYKIT_LOGO = "\n"+
            " __  __   ___                     .     .--.          \n" +
            "|  |/  `.'   `..-.          .-  .'|     |__|          \n" +
            "|   .-.  .-.   '\\ \\        / /.'  |     .--.     .|   \n" +
            "|  |  |  |  |  | \\ \\      / /<    |     |  |   .' |_  \n" +
            "|  |  |  |  |  |  \\ \\    / /  |   | ____|  | .'     | \n" +
            "|  |  |  |  |  |   \\ \\  / /   |   | \\ .'|  |'--.  .-' \n" +
            "|  |  |  |  |  |    \\ `  /    |   |/  . |  |   |  |   \n" +
            "|__|  |__|  |__|     \\  /     |    /\\  \\|__|   |  |   \n" +
            "                     / /      |   |  \\  \\      |  '.' \n" +
            "                 |`-' /       '    \\  \\  \\     |   /  \n" +
            "                  '..'       '------'  '---'   `'-'   ";
    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MykitLogo.class);

    public void logo() {
        String bannerText = buildBannerText();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(bannerText);
        } else {
            System.out.print(bannerText);
        }
    }

    private String buildBannerText() {
        return CommonConstant.LINE_SEPARATOR
                + CommonConstant.LINE_SEPARATOR
                + MYKIT_LOGO
                + CommonConstant.LINE_SEPARATOR
                + " :: Mykit :: (v" + VersionUtils.getVersion(getClass(), "1.0.0") + ")"
                + CommonConstant.LINE_SEPARATOR;
    }
}
