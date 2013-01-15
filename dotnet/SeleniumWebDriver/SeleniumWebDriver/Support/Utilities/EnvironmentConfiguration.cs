﻿using System.Configuration;

namespace SeleniumWebDriver.Support.Utilities
{
    public static class EnvironmentConfiguration
    {
        public static string GetEnvironment()
        {
            string environment;
            var _environment = ConfigurationManager.AppSettings["AppEnvironment"].ToLowerInvariant();

            switch (_environment)
            {
                case "integration":
                    environment = "";
                    break;

                default:
                    environment = "";
                    break;
            }

            return environment;
        }
    }
}
