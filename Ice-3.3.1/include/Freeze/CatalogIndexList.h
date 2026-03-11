// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

// Freeze types in this file:
// name="Freeze::CatalogIndexList", key="string", value="Ice::StringSeq"

#ifndef __CatalogIndexList_h__
#define __CatalogIndexList_h__

#include <Freeze/Map.h>
#include <Ice/BuiltinSequences.h>

#ifndef ICE_IGNORE_VERSION
#   if ICE_INT_VERSION / 100 != 303
#       error Ice version mismatch!
#   endif
#   if ICE_INT_VERSION % 100 > 50
#       error Beta header file detected
#   endif
#   if ICE_INT_VERSION % 100 < 1
#       error Ice patch level mismatch!
#   endif
#endif

#ifndef FREEZE_API
#   ifdef FREEZE_API_EXPORTS
#       define FREEZE_API ICE_DECLSPEC_EXPORT
#   else
#       define FREEZE_API ICE_DECLSPEC_IMPORT
#   endif
#endif

namespace Freeze
{

class FREEZE_API CatalogIndexListKeyCodec
{
public:

    static void write(const ::std::string&, Freeze::Key&, const ::Ice::CommunicatorPtr&);
    static void read(::std::string&, const Freeze::Key&, const ::Ice::CommunicatorPtr&);
    static const std::string& typeId();
};

class FREEZE_API CatalogIndexListValueCodec
{
public:

    static void write(const ::Ice::StringSeq&, Freeze::Value&, const ::Ice::CommunicatorPtr&);
    static void read(::Ice::StringSeq&, const Freeze::Value&, const ::Ice::CommunicatorPtr&);
    static const std::string& typeId();
};

typedef Freeze::Map< ::std::string, ::Ice::StringSeq, CatalogIndexListKeyCodec, CatalogIndexListValueCodec, Freeze::IceEncodingCompare > CatalogIndexList;

}

#endif
